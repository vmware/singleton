/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"context"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"strings"
	"testing"

	"sgtnserver/internal/config"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/dao/localbundle"
	"sgtnserver/modules/translation/dao/s3bundle"

	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/buf1024/golib/crypt"
	"github.com/go-test/deep"
	"github.com/golang/mock/gomock"
	"github.com/stretchr/testify/assert"
)

var (
	s3config = s3bundle.NewS3Config("", "", "", "", 3600, config.Settings.S3Bundle.Region)

	s3b = &s3bundle.S3Bundle{
		RootPrefix: config.Settings.S3Bundle.BundleRoot,
		Bucket:     &config.Settings.S3Bundle.BucketName,
		Config:     s3config}

	bucket     = &config.Settings.S3Bundle.BucketName
	rootPrefix = &config.Settings.S3Bundle.BundleRoot

	localBundle = localbundle.NewLocalBundle(config.Settings.LocalBundle.BasePath)
)

func TestDecryption(t *testing.T) {
	var (
		pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCB2ahoumYOFtIN5W1I8NIPDQNH/wK1YQsWtxqrXAu67XEg6rCm7Lwdj889L5kXuI3+nW93833zxb5+K1W//R+srfcwA/jZMqs1EVKyrdareWqVW0b6DvZFPV38UQVJmwfBJBkdUoTSZtqBrFhSFMfFWSG7Qoxz1NlJaJylkaEk3QIDAQAB"
		data   = "Jfe9vTfTsU6OQfZ4xikT+oKxytZOB1binD5wi10n2GMoI+4Jc/Yyy9x9WbTbzj1a4lM6nGBYnOQSjFn3e1QhcL8uj7eCg9mLt6bTec1FGi5ctPFrJexnkBX9lis+qQ1ntwkbf6h2g6U1SYN7N/t+/fq7ubybR6QdKY6hNwIJrCA="
	)

	dst := make([]byte, base64.StdEncoding.DecodedLen(len(pubKey)))
	l, err := base64.StdEncoding.Decode(dst, []byte(pubKey))
	assert.Nil(t, err)

	publicKey, err := x509.ParsePKIXPublicKey(dst[:l])
	assert.Nil(t, err)

	dst = make([]byte, base64.StdEncoding.DecodedLen(len(data)))
	l, err = base64.StdEncoding.Decode(dst, []byte(data))
	assert.Nil(t, err)

	result, err := crypt.PublicDecrypt(publicKey.(*rsa.PublicKey), dst[:l])
	assert.Nil(t, err)
	assert.Equal(t, "abc", string(result))
}

type MockAWSError struct {
	code string
}

// Returns the short phrase depicting the classification of the error.
func (e MockAWSError) Code() string {
	return e.code
}

// Returns the error details message.
func (e MockAWSError) Message() string {
	return ""
}

func (e MockAWSError) OrigErr() error {
	return nil
}
func (e MockAWSError) Error() string {
	return e.code
}

var bundleFile = `{
	"component": "sunglow",
	"messages": {
	  "plural.files": "{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}",
	  "message": "Message-en",
	  "pagination": "{0} - {1} of {2} customers",
	  "one.arg": "test one argument {0}"
	},
	"locale": "%s"
  }`

func TestGetBundleFromS3(t *testing.T) {
	s3api := createMock(t)
	tests := []struct {
		testName        string
		id              translation.BundleID
		mockOutput      string
		mockReturnError error
		wantedCode      int
	}{
		{testName: "Normal", mockOutput: fmt.Sprintf(bundleFile, Locale), mockReturnError: nil, wantedCode: http.StatusOK,
			id: translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}},
		{testName: "FileContentWrong", mockOutput: fmt.Sprintf(bundleFile, "anotherLocale"), mockReturnError: nil, wantedCode: http.StatusOK,
			id: translation.BundleID{Name: Name, Version: Version, Locale: "yue", Component: Component}},
		{testName: "ReturnNotFound", mockOutput: fmt.Sprintf(bundleFile, Locale), mockReturnError: &types.NoSuchKey{}, wantedCode: sgtnerror.StatusNotFound.Code(),
			id: translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}},
		{testName: "ReturnOtherError", mockOutput: fmt.Sprintf(bundleFile, Locale), mockReturnError: errors.New("other type of error"), wantedCode: http.StatusInternalServerError,
			id: translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			mockInput := s3.GetObjectInput{Bucket: bucket, Key: s3b.GetKey(&tt.id)}

			// Set expect
			s3api.EXPECT().GetObject(context.TODO(), gomock.Eq(&mockInput)).Return(&s3.GetObjectOutput{Body: ioutil.NopCloser(strings.NewReader(tt.mockOutput))}, tt.mockReturnError)

			// Test
			b, err := s3b.GetBundle(context.TODO(), &tt.id)
			if tt.wantedCode == http.StatusOK {
				assert.Nil(t, err)
				assert.Equal(t, tt.id.Locale, b.ID.Locale)
				assert.Equal(t, tt.id.Component, b.ID.Component)
			} else {
				assert.Equal(t, tt.wantedCode, sgtnerror.GetCode(err))
				assert.Nil(t, b)
			}
		})
	}
}

func TestPutBundleToS3(t *testing.T) {
	s3api := createMock(t)

	tests := []struct {
		testName        string
		id              translation.BundleID
		bundleToPut     string
		mockReturnError error
		wantedCode      int
	}{
		{testName: "Normal", bundleToPut: fmt.Sprintf(bundleFile, Locale), mockReturnError: nil, wantedCode: http.StatusOK,
			id: translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}},
		{testName: "ReturnError", bundleToPut: fmt.Sprintf(bundleFile, Locale), mockReturnError: errors.New("error"), wantedCode: http.StatusInternalServerError,
			id: translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}},
	}
	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			bf := translation.BundleFile{}
			json.UnmarshalFromString(tt.bundleToPut, &bf)
			bundleToPut := translation.Bundle{ID: tt.id, Messages: bf.Messages}

			s3api.EXPECT().PutObject(context.TODO(), gomock.AssignableToTypeOf(&s3.PutObjectInput{})).Return(nil, tt.mockReturnError)

			err := s3b.PutBundle(context.TODO(), &bundleToPut)
			if tt.wantedCode == http.StatusOK {
				assert.Nil(t, err)
			} else {
				assert.Equal(t, tt.wantedCode, sgtnerror.GetCode(err))
			}
		})
	}
}

func TestGetBundleInfoOfS3(t *testing.T) {
	getExpectedInfo := func() *translation.BundleInfo {
		localInfo, err := localBundle.GetBundleInfo(context.TODO())
		assert.Nil(t, err)

		info := translation.NewBundleInfo()
		pInfo, ok := localInfo.GetProductInfo(Name)
		if ok {
			for _, relName := range pInfo.GetReleaseNames().Values() {
				if rInfo, ok := localInfo.GetReleaseInfo(Name, relName.(string)); ok {
					for _, bd := range rInfo.AvailableBundles.Values() {
						locale := bd.(translation.CompactBundleID).Locale
						component := bd.(translation.CompactBundleID).Component

						bundleID := translation.BundleID{Name: Name,
							Version: relName.(string), Locale: locale, Component: component}

						info.AddBundle(&bundleID)
					}
				}
			}
		}
		return info
	}
	getKeysFromInfo := func(info *translation.BundleInfo) []string {
		var keys []string
		pInfo, ok := info.GetProductInfo(Name)
		if ok {
			for _, relName := range pInfo.GetReleaseNames().Values() {
				if rInfo, ok := info.GetReleaseInfo(Name, relName.(string)); ok {
					for _, bd := range rInfo.AvailableBundles.Values() {
						locale := bd.(translation.CompactBundleID).Locale
						component := bd.(translation.CompactBundleID).Component

						bundleID := translation.BundleID{Name: Name,
							Version: relName.(string), Locale: locale, Component: component}

						keys = append(keys, *s3b.GetKey(&bundleID))
					}
				}
			}
		}
		return keys
	}
	sendKeysMock := func(keys []string, err error) func(context.Context, *s3.ListObjectsV2Input, ...func(*s3.Options)) (*s3.ListObjectsV2Output, error) {
		return func(ctx context.Context, i *s3.ListObjectsV2Input, options ...func(*s3.Options)) (*s3.ListObjectsV2Output, error) {
			var output s3.ListObjectsV2Output
			for i := 0; i < len(keys); i++ {
				output.Contents = append(output.Contents, types.Object{Key: &keys[i]})
			}
			return &output, nil
		}
	}

	expectedInfo := getExpectedInfo()
	keys := getKeysFromInfo(expectedInfo)
	expectedInput := &s3.ListObjectsV2Input{Bucket: bucket, Prefix: rootPrefix}
	createMock(t).EXPECT().ListObjectsV2(context.TODO(), gomock.Eq(expectedInput), gomock.Any()).DoAndReturn(sendKeysMock(keys, nil))

	// Test
	info, err := s3b.GetBundleInfo(context.TODO())
	assert.Nil(t, err)
	if diff := deep.Equal(expectedInfo, info); diff != nil {
		t.Error(diff)
	}
}

func createMock(t *testing.T) *MockClientAPI {
	ctrl := gomock.NewController(t)

	// Create mock object
	s3api := NewMockClientAPI(ctrl)
	s3bundle.GetS3Client = func(*s3bundle.S3Config) s3bundle.ClientAPI {
		return s3api
	}

	return s3api
}

var _ = func() bool {
	testing.Init()
	return true
}()
