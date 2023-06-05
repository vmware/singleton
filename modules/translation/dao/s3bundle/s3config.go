/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package s3bundle

import (
	"crypto/rsa"
	"crypto/x509"
	"io/ioutil"

	"sgtnserver/internal/logger"
)

type (
	S3Config struct {
		publicKeyFile, accessKey, secretKey, roleArn, region string
		sessionDuration int32
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
		fileContent, err := ioutil.ReadFile(config.publicKeyFile)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}

		decodedData, err := Base64Decode(fileContent)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}

		pubKey, err := x509.ParsePKIXPublicKey(decodedData)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}
		rsaPubKey := pubKey.(*rsa.PublicKey)

		// Get AccessKey
		accessKey, err := Decrypt([]byte(config.accessKey), rsaPubKey)
		if err != nil {
			logger.Log.Fatal(err.Error())
		}
		config.accessKey = string(accessKey)

		// Get SecretKey
		secretKey, err := Decrypt([]byte(config.secretKey), rsaPubKey)
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
