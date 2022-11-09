/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package s3bundle

import (
	"context"
	"sgtnserver/internal/logger"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/sts"
	"github.com/aws/aws-sdk-go-v2/service/sts/types"
)

const sessionDurationSeconds = 3600

var (
	updateTimeAdvance, _     = time.ParseDuration("60s")
	expirationTimeAdvance, _ = time.ParseDuration("10s")

	clientInst RoleClient
)

type RoleClient struct {
	*s3.Client
	RoleCredentials *types.Credentials
}

var GetS3Client = func(s3Settings *S3Config) ClientAPI {
	currentInst := clientInst
	diff := time.Until(*currentInst.RoleCredentials.Expiration)
	if diff >= expirationTimeAdvance {
		if diff <= updateTimeAdvance {
			go newS3Client(s3Settings)
		}
		return currentInst
	}

	return newS3Client(s3Settings)
}

func newS3Client(s3Settings *S3Config) RoleClient {
	stsClient := sts.New(sts.Options{
		Region:      s3Settings.GetRegion(),
		Credentials: aws.NewCredentialsCache(credentials.NewStaticCredentialsProvider(s3Settings.GetAccessKey(), s3Settings.GetSecretKey(), "")),
	})

	input := &sts.AssumeRoleInput{
		RoleArn:         aws.String(s3Settings.GetRoleArn()),
		RoleSessionName: aws.String("singleton_s3"),
		DurationSeconds: aws.Int32(sessionDurationSeconds),
	}

	roleOutput, err := stsClient.AssumeRole(context.Background(), input)
	if err != nil {
		logger.Log.Fatal("fail to assume role, " + err.Error())
	}

	creProvider := credentials.NewStaticCredentialsProvider(*roleOutput.Credentials.AccessKeyId,
		*roleOutput.Credentials.SecretAccessKey, *roleOutput.Credentials.SessionToken)
	clientInst = RoleClient{
		Client:          s3.New(s3.Options{Region: s3Settings.GetRegion(), Credentials: aws.NewCredentialsCache(creProvider)}),
		RoleCredentials: roleOutput.Credentials,
	}

	return clientInst
}
