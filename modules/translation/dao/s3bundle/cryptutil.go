/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package s3bundle

import (
	"crypto/rsa"
	"encoding/base64"

	"github.com/buf1024/golib/crypt"
)

func Decrypt(data []byte, publicKey *rsa.PublicKey) (result []byte, err error) {
	decoded, err := Base64Decode(data)
	if err == nil {
		result, err = crypt.PublicDecrypt(publicKey, decoded)
	}

	return
}

func Base64Decode(data []byte) ([]byte, error) {
	dst := make([]byte, base64.StdEncoding.DecodedLen(len(data)))
	l, err := base64.StdEncoding.Decode(dst, data)
	return dst[:l], err
}
