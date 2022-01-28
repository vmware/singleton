/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
/**
 * 
 *
 * @author shihu
 *
 */
public class VipRSAUtils {
	public static final String KEY_ALGORITHM = "RSA";



	
	
	
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
	public static String rsaEncrypt(String encryptStr, String pubKeyStr) {

		System.out.println("--------------pubkey------");

		String[] pubkeysubstrs = pubKeyStr.split("\r");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pubkeysubstrs.length; i++) {
			  System.out.println("pubkey line"+pubkeysubstrs[i]);
			
			if (!pubkeysubstrs[i].contains("PUBLIC KEY")) {

				sb.append(pubkeysubstrs[i] + "\r");
			}
		}

		System.out.println(sb.toString());

		byte[] pubbytes = null;
		try {
			pubbytes = Base64.getMimeDecoder().decode(sb.toString().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubbytes);
		PublicKey publicKey = null;
		if(keyFactory != null) {
		    try {
		        publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		    } catch (InvalidKeySpecException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }   
		}else {
		    return null;
		}
		if (publicKey != null) {
			
			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cipher != null) {
			    try {
	                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	            } catch (InvalidKeyException e1) {
	                // TODO Auto-generated catch block
	                e1.printStackTrace();
	            }
			}else {
			    return null;
			}
			
			byte[] resultBytes = null;
			try {
				resultBytes = cipher.doFinal(encryptStr.getBytes(Charset.forName("UTF-8")));
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Base64.getEncoder().encodeToString(resultBytes);
		}
		return null;
	}

	private static RSAPrivateKey getPrivateKey(String privateString)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// PKCS#8format key string 
   
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(privateString));
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		return key;
	}

	/**
	 * decode
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
	public static String rsaDecrypt(String deStr, String priKeyStr) {
		RSAPrivateKey privateKey = null;
		try {
			privateKey = getPrivateKey(priKeyStr);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (privateKey != null) {
			
			Cipher cipher;
			try {
				cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] resultBytes = cipher.doFinal(Base64.getDecoder().decode(deStr));
				return new String(resultBytes, Charset.forName("UTF-8"));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}


}
