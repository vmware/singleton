/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package s3bundle

import (
	"bytes"
	"context"
	"regexp"
	"strings"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/awserr"
	"github.com/aws/aws-sdk-go/aws/credentials"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3"
	"github.com/aws/aws-sdk-go/service/s3/s3iface"
	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
)

var json = jsoniter.ConfigDefault

const slash = "/"

type (
	S3Bundle struct {
		RootPrefix string
		Bucket     *string
		S3Client   s3iface.S3API
		Config     *S3Config
	}
)

// NewS3Bundle ...
func NewS3Bundle(rootPrefix, bucket string, config *S3Config) *S3Bundle {
	// Normalize base path
	if !strings.HasSuffix(rootPrefix, slash) {
		rootPrefix += slash
	}
	rootPrefix = regexp.MustCompile(`^(/+|\./)`).ReplaceAllLiteralString(rootPrefix, "")

	// Create S3 session
	awsConfig := aws.Config{
		Credentials: credentials.NewStaticCredentials(config.GetAccessKey(), config.GetSecretKey(), ""),
		Region:      aws.String(config.GetRegion()),
	}

	// Create bundle instance
	bundle := &S3Bundle{
		RootPrefix: rootPrefix,
		Bucket:     aws.String(bucket),
		S3Client:   s3.New(session.Must(session.NewSession(&awsConfig))),
		Config:     config}

	// check bucket
	_, err := bundle.S3Client.HeadBucket(&s3.HeadBucketInput{Bucket: bundle.Bucket})
	if err != nil {
		if awsErr, ok := err.(awserr.Error); ok {
			switch awsErr.Code() {
			case s3.ErrCodeNoSuchBucket:
				if err = bundle.createBucket(); err != nil {
					createErr, ok := err.(awserr.Error)
					if !ok || (createErr.Code() != s3.ErrCodeBucketAlreadyOwnedByYou) {
						logger.Log.Fatal(err.Error())
					}
				}
			default:
				logger.Log.Fatal(err.Error())
			}
		} else {
			logger.Log.Fatal(err.Error())
		}

	}

	return bundle
}

// GetBundleInfo ...
func (b *S3Bundle) GetBundleInfo(ctx context.Context) (*translation.BundleInfo, error) {
	bundleInfo := translation.NewBundleInfo()
	err := b.S3Client.ListObjectsV2Pages(
		&s3.ListObjectsV2Input{Bucket: b.Bucket, Prefix: &b.RootPrefix},
		func(page *s3.ListObjectsV2Output, lastPage bool) bool {
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

			return !lastPage
		})

	if err != nil {
		returnErr := sgtnerror.StatusInternalServerError.WrapError(err)
		logger.FromContext(ctx).Error(returnErr.Error())
		return nil, returnErr
	}

	return bundleInfo, nil
}

// GetBundle ...
func (b *S3Bundle) GetBundle(ctx context.Context, id *translation.BundleID) (bundle *translation.Bundle, returnErr error) {
	input := s3.GetObjectInput{Bucket: b.Bucket, Key: b.GetKey(id)}
	output, err := b.S3Client.GetObject(&input)
	if err == nil {
		defer output.Body.Close()

		bf := translation.BundleFile{}
		if err = json.NewDecoder(output.Body).Decode(&bf); err == nil {
			if !strings.EqualFold(bf.Locale, id.Locale) || !strings.EqualFold(bf.Component, id.Component) {
				logger.FromContext(ctx).Error("Bundle file content is wrong!",
					zap.String(translation.Locale, bf.Locale+"?="+id.Locale),
					zap.String(translation.Component, bf.Component+"?="+id.Component))
			}
			return &translation.Bundle{ID: *id, Messages: bf.Messages}, nil
		}
	}

	if awsErr, ok := err.(awserr.Error); ok && awsErr.Code() == s3.ErrCodeNoSuchKey {
		returnErr = sgtnerror.StatusNotFound.WrapErrorWithMessage(err, translation.FailToReadBundle, id.Name, id.Version, id.Component, id.Locale)
	} else {
		returnErr = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, translation.FailToReadBundle, id.Name, id.Version, id.Component, id.Locale)
	}
	logger.FromContext(ctx).Error(returnErr.Error())

	return nil, returnErr
}

// PutBundle ...
func (b *S3Bundle) PutBundle(ctx context.Context, bundleData *translation.Bundle) (returnErr error) {
	bundle := &translation.BundleFile{Component: bundleData.ID.Component, Locale: bundleData.ID.Locale, Messages: bundleData.Messages}
	bts, err := json.MarshalIndent(bundle, "", "    ")
	if err != nil {
		returnErr = sgtnerror.StatusBadRequest.WrapError(err)
	} else {
		input := s3.PutObjectInput{Bucket: b.Bucket, Key: b.GetKey(&bundleData.ID), Body: bytes.NewReader(bts)}
		if _, err = b.S3Client.PutObject(&input); err != nil {
			returnErr = sgtnerror.StatusInternalServerError.WrapError(err)
		}
	}

	if returnErr != nil {
		logger.FromContext(ctx).Error(returnErr.Error())
	}

	return returnErr
}

// func (b *S3Bundle) DeleteBundle(ctx context.Context, id *translation.BundleID) error {
// 	input := s3.DeleteObjectInput{Bucket: b.Bucket, Key: b.GetKey(id)}
// 	_, err := b.S3Client.DeleteObject(&input)
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
	createBucketConfig := s3.CreateBucketConfiguration{LocationConstraint: &b.Config.region}
	input := &s3.CreateBucketInput{
		Bucket:                    b.Bucket,
		CreateBucketConfiguration: &createBucketConfig}
	_, err := b.S3Client.CreateBucket(input)
	return err
}
