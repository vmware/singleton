/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package s3bundle

import (
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
)

type (
	S3Config struct {
		publicKeyFile, accessKey, secretKey, roleArn, region string
		sessionDuration                                      int32
	}
)

func NewS3Config(publicKeyFile, accessKey, secretKey, roleArn string, sessionDuration int32, region string) *S3Config {
	config := &S3Config{
		publicKeyFile:   publicKeyFile,
		accessKey:       accessKey,
		secretKey:       secretKey,
		roleArn:         roleArn,
		sessionDuration: sessionDuration,
		region:          region,
	}

	if len(config.publicKeyFile) != 0 {
		// Get public key
		rsaPubKey, err := common.GetPublicKeyFromFile(config.publicKeyFile)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}

		// Get AccessKey
		accessKey, err := common.Decrypt([]byte(config.accessKey), rsaPubKey)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}
		config.accessKey = string(accessKey)

		// Get SecretKey
		secretKey, err := common.Decrypt([]byte(config.secretKey), rsaPubKey)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}
		config.secretKey = string(secretKey)
	}

	return config
}

func (config *S3Config) GetAccessKey() string {
	return config.accessKey
}

func (config *S3Config) GetSecretKey() string {
	return config.secretKey
}

func (config *S3Config) GetRoleArn() string {
	return config.roleArn
}

func (config *S3Config) GetRegion() string {
	return config.region
}
