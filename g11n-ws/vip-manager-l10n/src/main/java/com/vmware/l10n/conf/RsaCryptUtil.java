/**
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * it use to decode the access key and Secret key
 *
 */
public class RsaCryptUtil {
	private RsaCryptUtil() {
	}

	private static final String CHARSET = "UTF-8";

	/**
	 * decode Data by public key
	 * @param data
	 * @param publicInfoStr
	 * @return
	 */
	public static String decryptData(String data, String publicInfoStr)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		byte[] encryptDataBytes = Base64.decodeBase64(data);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicInfoStr));
		return new String(cipher.doFinal(encryptDataBytes), CHARSET);
	}

	/**
	 * get public key from base64 code
	 *
	 * @param base64PublicKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private static PublicKey getPublicKey(String base64PublicKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(base64PublicKey));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);
	}

	/**
	 * get public key string from file
	 *
	 * @param file
	 * @return
	 */
	public static String getPublicKeyStrFromFile(File file) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String input;
			while ((input = br.readLine()) != null) {
				sb.append(input);
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
