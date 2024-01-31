/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.security;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;



public class RSAUtils {

	private static final String CHARSET = "UTF-8";
	 /**
     * encrypt Data by private key
     * @param data
     * @param privateInfoStr
     * @return
     * @throws IOException
     */
    public static String encryptData(String data, String privateInfoStr) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
 
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(privateInfoStr));
        return Base64.encodeBase64String(cipher.doFinal(data.getBytes(CHARSET)));
    }
    
    
    
    private static PrivateKey getPrivateKey(String base64PrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(base64PrivateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
 
	
	
	
	
	
	/**
	 * decode Data by public key
	 * 
	 * @param data
	 * @param publicInfoStr
	 * @return
	 */
	public static String decryptData(String data, String publicInfoStr)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		byte[] encryptDataBytes = Base64.decodeBase64(data);
		if(data.equals(Base64.encodeBase64String(encryptDataBytes))){
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicInfoStr));
			return new String(cipher.doFinal(encryptDataBytes), CHARSET);
		}else {
			throw new BadPaddingException("The token is modified!");
		}
		
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
	 * @param inputStream
	 * @return
	 */
	public static String getKeyStrFromInputStream(InputStream inputStream) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
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
