/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package s3bundle

import (
	"bytes"
	"context"
	"errors"
	"regexp"
	"strings"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"
)

var json = jsoniter.ConfigDefault

const slash = "/"

type S3Bundle struct {
	RootPrefix string
	Bucket     *string
	Config     *S3Config
}

// NewS3Bundle ...
func NewS3Bundle(rootPrefix, bucket string, config *S3Config) *S3Bundle {
	// Normalize base path
	if !strings.HasSuffix(rootPrefix, slash) {
		rootPrefix += slash
	}
	rootPrefix = regexp.MustCompile(`^(/+|\./)`).ReplaceAllLiteralString(rootPrefix, "")

	// Create bundle instance
	bundle := &S3Bundle{RootPrefix: rootPrefix, Bucket: aws.String(bucket), Config: config}

	// check bucket
	_, err := newS3Client(config).HeadBucket(context.Background(), &s3.HeadBucketInput{Bucket: bundle.Bucket})
	var noSuchBucketErr *types.NoSuchBucket
	if errors.As(err, &noSuchBucketErr) {
		err = bundle.createBucket()
		var alreadyOwned *types.BucketAlreadyOwnedByYou
		if !errors.As(err, &alreadyOwned) {
			logger.Log.Fatal(err.Error())
		}
	} else if err != nil {
		logger.Log.Fatal(err.Error())
	}

	return bundle
}

// GetBundleInfo ...
func (b *S3Bundle) GetBundleInfo(ctx context.Context) (*translation.BundleInfo, error) {
	bundleInfo := translation.NewBundleInfo()

	p := s3.NewListObjectsV2Paginator(GetS3Client(b.Config), &s3.ListObjectsV2Input{Bucket: b.Bucket, Prefix: &b.RootPrefix})
	for p.HasMorePages() {
		page, err := p.NextPage(ctx)
		if err != nil {
			wrapErr := sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, translation.FailToGetBundleInfo)
			logger.FromContext(ctx).Error(wrapErr.Error())
			continue
		}

		for _, entry := range page.Contents {
			p := strings.TrimPrefix(*entry.Key, b.RootPrefix)
			parts := strings.Split(p, slash)
			if len(parts) < 4 {
				continue
			}

			bundleName := parts[3]
			if strings.HasPrefix(bundleName, translation.BundlePrefix) &&
				strings.HasSuffix(bundleName, translation.BundleSuffix) {
				locale := bundleName[len(translation.BundlePrefix) : len(bundleName)-len(translation.BundleSuffix)]
				bundleInfo.AddBundle(&translation.BundleID{
					Name:      parts[0],
					Version:   parts[1],
					Component: parts[2],
					Locale:    locale})
			}
		}
	}

	return bundleInfo, nil
}

// GetBundle ...
func (b *S3Bundle) GetBundle(ctx context.Context, id *translation.BundleID) (bundle *translation.Bundle, returnErr error) {
	bf := translation.BundleFile{}
	err := b.ReadJSONFile(ctx, *b.GetKey(id), &bf)
	if err == nil {
		if !strings.EqualFold(bf.Locale, id.Locale) || !strings.EqualFold(bf.Component, id.Component) {
			logger.FromContext(ctx).Error("Bundle file content is wrong!",
				zap.String(translation.Locale, bf.Locale+"?="+id.Locale),
				zap.String(translation.Component, bf.Component+"?="+id.Component))
		}
		return &translation.Bundle{ID: *id, Messages: bf.Messages}, nil
	}

	return nil, err
}

// PutBundle ...
func (b *S3Bundle) PutBundle(ctx context.Context, bundleData *translation.Bundle) (returnErr error) {
	bundle := &translation.BundleFile{Component: bundleData.ID.Component, Locale: bundleData.ID.Locale, Messages: bundleData.Messages}
	bts, err := json.MarshalIndent(bundle, "", "    ")
	if err != nil {
		returnErr = sgtnerror.StatusBadRequest.WrapErrorWithMessage(err, translation.WrongBundleContent, bundleData.ID.Name, bundleData.ID.Version, bundleData.ID.Locale, bundleData.ID.Component)
	} else {
		input := s3.PutObjectInput{Bucket: b.Bucket, Key: b.GetKey(&bundleData.ID), Body: bytes.NewReader(bts)}
		if _, err = GetS3Client(b.Config).PutObject(ctx, &input); err != nil {
			returnErr = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, translation.FailToStoreBundle, bundleData.ID.Name, bundleData.ID.Version, bundleData.ID.Locale, bundleData.ID.Component)
		}
	}

	if returnErr != nil {
		logger.FromContext(ctx).Error(returnErr.Error())
	}

	return returnErr
}

// func (b *S3Bundle) DeleteBundle(ctx context.Context, id *translation.BundleID) error {
// 	input := s3.DeleteObjectInput{Bucket: b.Bucket, Key: b.GetKey(id)}
// 	_, err := GetS3Client(b.Config).DeleteObject(&input)
// 	if err == nil {
// 		return nil
// 	}

// 	returnErr := sgtnerror.StatusInternalServerError.WrapError(err)
// 	logger.FromContext(ctx).Error(returnErr.Error())
// 	return returnErr
// }

func (b *S3Bundle) GetKey(id *translation.BundleID) *string {
	str := b.RootPrefix +
		id.Name + slash +
		id.Version + slash +
		id.Component + slash +
		translation.GetBundleFilename(id.Locale)
	return aws.String(str)
}

func (b *S3Bundle) createBucket() error {
	createBucketConfig := types.CreateBucketConfiguration{LocationConstraint: types.BucketLocationConstraint(b.Config.region)}
	input := s3.CreateBucketInput{
		Bucket:                    b.Bucket,
		CreateBucketConfiguration: &createBucketConfig}
	_, err := GetS3Client(b.Config).CreateBucket(context.Background(), &input)
	return err
}

func (b *S3Bundle) ReadJSONFile(ctx context.Context, filePath string, data interface{}) error {
	input := s3.GetObjectInput{Bucket: b.Bucket, Key: &filePath}
	output, err := GetS3Client(b.Config).GetObject(ctx, &input)
	if err == nil {
		defer output.Body.Close()

		if err = json.NewDecoder(output.Body).Decode(data); err == nil {
			return nil
		}
	}

	var noSuchKeyErr *types.NoSuchKey
	if errors.As(err, &noSuchKeyErr) {
		err = sgtnerror.StatusNotFound.WrapErrorWithMessage(err, translation.FailToReadFile, filePath)
	} else {
		err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, translation.FailToReadFile, filePath)
	}
	logger.FromContext(ctx).Error(err.Error())

	return err
}

func (b *S3Bundle) GetVersionInfo(ctx context.Context, name, version string) (data map[string]interface{}, err error) {
	filePath := b.RootPrefix + name + slash + version + slash + translation.VersionInfoFile
	data = make(map[string]interface{})
	err = b.ReadJSONFile(ctx, filePath, &data)
	return
}
