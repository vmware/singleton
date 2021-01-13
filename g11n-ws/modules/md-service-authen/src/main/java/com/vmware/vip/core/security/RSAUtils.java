/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtils {

 public static String abc = "-----BEGIN PUBLIC KEY-----\r" + 
 		"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+6IEqLH5Np2dHnkqMfwlfrvlj\r" + 
 		"KC7hIrxEePH8cv7EDWQ0/tEz7c9bKe9i9ryhVBsgUdomKaJyCdZgs/r+hARhpUfM\r" + 
 		"zTjbB76ac2+HCP0NQ0ZLIcch2lcYNcE0Lftf65TRfnjo3z/u4VLJLQoQNIxACEYW\r" + 
 		"2nPOVNB2AD5VOsd1wwIDAQAB\r" + 
 		"-----END PUBLIC KEY-----\r";
	private static Map<String, Object> keysMap = makeKeyfile();
    private static Logger logger = LoggerFactory.getLogger(RSAUtils.class);
    
	private static Map<String, Object> makeKeyfile() {
		
		Map<String, Object> keyMap = new HashMap<String, Object>();
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			return null;
		}
		
		keyPairGen.initialize(1024);
		
		KeyPair keyPair = keyPairGen.generateKeyPair();

		
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

	
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		
		keyMap.put("privateKey", privateKey);
		keyMap.put("pubKey", publicKey);

		return keyMap;

	}

	

	
	/**
	 * 
	 * 
	 * @param publicKey
	 * @param srcBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String rsaEncrypt(String encryptStr) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		RSAPublicKey publicKey = (RSAPublicKey) keysMap.get("pubKey");
		if (publicKey != null) {
			
			Cipher cipher = Cipher.getInstance("RSA");
			
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] resultBytes = cipher.doFinal(encryptStr.getBytes(Charset.forName("UTF-8")));
			return Base64.getEncoder().encodeToString(resultBytes);
		}
		return null;
	}

	/**
	 *
	 * 
	 * @param publicKey
	 * @param srcBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException 
	 */
	public static String rsaEncrypt(String encryptStr, String pubKeyStr)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeySpecException {

	
		BufferedReader br = new BufferedReader(new StringReader(pubKeyStr));
		StringBuilder sb = new StringBuilder();
				String line =null;
		try {
			while( (line= br.readLine()) != null) {
			//	System.out.println(line);
				if(!line.contains("PUBLIC KEY")) {
    				
					sb.append(line+"\r");
    			}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}			
	
	//	System.out.println("--------------pubkey------");
	
		try {
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.error(e1.getMessage(), e1);
		}
		
       byte[] pubbytes = null;
	try {
		pubbytes = Base64.getMimeDecoder().decode(sb.toString().getBytes("utf-8"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		logger.error(e.getMessage(), e);
	}    
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
        X509EncodedKeySpec keySpec= new X509EncodedKeySpec(pubbytes);  
        PublicKey publicKey= (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        if (publicKey != null) {
			
			Cipher cipher = Cipher.getInstance("RSA");
			
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] resultBytes = cipher.doFinal(encryptStr.getBytes(Charset.forName("UTF-8")));
			return Base64.getEncoder().encodeToString(resultBytes);
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param privateKey
	 * @param srcBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String rsaDecrypt(String deStr, String privateKeyStr) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		RSAPrivateKey privateKey = (RSAPrivateKey) keysMap.get("privateKey");
		if (privateKey != null) {
			
			Cipher cipher = Cipher.getInstance("RSA");
		
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] resultBytes = cipher.doFinal(Base64.getDecoder().decode(deStr));
			return new String(resultBytes, Charset.forName("UTF-8"));
		}
		return null;
	}
	
	/**
	 * 解密
	 * 
	 * @param privateKey
	 * @param srcBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String rsaDecrypt(String deStr) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		RSAPrivateKey privateKey = (RSAPrivateKey) keysMap.get("privateKey");
		if (privateKey != null) {
		
			Cipher cipher = Cipher.getInstance("RSA");
			
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] resultBytes = cipher.doFinal(Base64.getDecoder().decode(deStr));
			return new String(resultBytes, Charset.forName("UTF-8"));
		}
		return null;
	}



}
