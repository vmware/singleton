/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package dao

import (
	"strings"

	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/dao/decorator"
	"sgtnserver/modules/translation/dao/localbundle"
	"sgtnserver/modules/translation/dao/s3bundle"
)

const bundleInLocal = "local"
const bundleInS3 = "s3"

var inst translation.MessageOrigin

func GetInst() translation.MessageOrigin {
	return inst
}

func init() {
	if strings.EqualFold(config.Settings.BundleLocation, bundleInLocal) {
		inst = localbundle.NewLocalBundle(config.Settings.LocalBundle.BasePath)
	} else if strings.EqualFold(config.Settings.BundleLocation, bundleInS3) {
		s3config := s3bundle.NewS3Config(
			config.Settings.S3Bundle.PublicKeyFile,
			config.Settings.S3Bundle.AccessKey,
			config.Settings.S3Bundle.SecretKey,
			config.Settings.S3Bundle.RoleArn,
			config.Settings.S3Bundle.SessionDuration,
			config.Settings.S3Bundle.Region)

		inst = s3bundle.NewS3Bundle(
			config.Settings.S3Bundle.BundleRoot,
			config.Settings.S3Bundle.BucketName,
			s3config)
	} else {
		logger.Log.Fatal("Wrong bundle location: " + config.Settings.BundleLocation)
	}

	inst = decorator.NewDAODecorator(inst)
}
