/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package common

import (
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"os"

	"github.com/buf1024/golib/crypt"
)

func Encrypt(data []byte, key *rsa.PrivateKey) (result []byte, err error) {
	encrypted, err := crypt.PrivateEncrypt(key, data)
	if err != nil {
		return nil, err
	}

	return Base64Encode(encrypted), nil
}

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

func Base64Encode(data []byte) []byte {
	dst := make([]byte, base64.StdEncoding.EncodedLen(len(data)))
	base64.StdEncoding.Encode(dst, data)
	return dst
}

func GetPublicKeyFromFile(filePath string) (*rsa.PublicKey, error) {
	fileContent, err := os.ReadFile(filePath)
	if err != nil {
		return nil, err
	}

	decodedData, err := Base64Decode(fileContent)
	if err != nil {
		return nil, err
	}

	key, err := x509.ParsePKIXPublicKey(decodedData)
	if err != nil {
		return nil, err
	}

	return key.(*rsa.PublicKey), nil
}

func GetPrivateKeyFromFile(filePath string) (*rsa.PrivateKey, error) {
	fileContent, err := os.ReadFile(filePath)
	if err != nil {
		return nil, err
	}

	decodedData, err := Base64Decode(fileContent)
	if err != nil {
		return nil, err
	}

	key, err := x509.ParsePKCS8PrivateKey(decodedData)
	if err != nil {
		return nil, err
	}

	return key.(*rsa.PrivateKey), nil
}
