---
title: "Enable S3 Key Encrypted"
date: 2021-02-24T16:00:33+08:00
draft: false
---
# Encrypt AWS S3 accesskey and secretkey in Singleton Service

## Prerequisites

- [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (Java 9+ are not supported, will get compiler issue)

- Singleton S3 I18n Service Build

- S3 configurations (accessKey, secretkey, region, bucketName)


## 1. Encrypt AWS S3 accessKey and secretkey

 You can use the java language RsaCryptUtils to generate private.key and public.key.

The demo code as following:

```
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
public class RsaCryptUtils {
    private static final String CHARSET = "utf-8";
    private static final Base64.Decoder decoder64 = Base64.getDecoder();
    private static final Base64.Encoder encoder64 = Base64.getEncoder();
    /**
     * create SecretKey
     * @param keySize
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateSecretKey(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        return new SecretKey(encoder64.encodeToString(publicKey.getEncoded()),     encoder64.encodeToString(privateKey.getEncoded()));
    }
    /**
     * encrypt Data by private key
     * @param data
     * @param privateInfoStr
     * @return
     * @throws IOException
     * @throws InvalidCipherTextException
     */
    public static String encryptData(String data, String privateInfoStr) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
     
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(privateInfoStr));
        return encoder64.encodeToString(cipher.doFinal(data.getBytes(CHARSET)));
    }
     
    /**
     * decrypt Data by public key
     * @param data
     * @param publicInfoStr
     * @return
     */
    public static String decryptData(String data, String publicInfoStr) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        byte[] encryptDataBytes=decoder64.decode(data.getBytes(CHARSET));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicInfoStr));
        return new String(cipher.doFinal(encryptDataBytes), CHARSET);
    }
    private static PublicKey getPublicKey(String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    private static PrivateKey getPrivateKey(String base64PrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
     
    /**
     * Secret key model
     */
    public static class SecretKey {
        /**
         * public key
         */
        private String publicKey;
        /**
         *private Key
         */
        private String privateKey;
     
        public SecretKey(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
     
        public String getPublicKey() {
            return publicKey;
        }
     
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
     
        public String getPrivateKey() {
            return privateKey;
        }
     
        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
     
        @Override
        public String toString() {
            return "SecretKey{" +
                    "publicKey='" + publicKey + '\'' +
                    ", privateKey='" + privateKey + '\'' +
                    '}';
        }
    }
    public static void main(String[] args) throws Exception {
    	SecretKey sk = generateSecretKey(1024);
    	System.out.println(sk.getPublicKey());
    	String encrytContent = "";
    	String result = encryptData(encrytContent, sk.getPrivateKey());
    	System.out.println(result);
    }

}
```

Generate the private.key and public.key. 
- Get the public key use demo code getPublicKey() method 

- Get the private key use demo code getPrivateKey() method.  

  

You need to output private key and public key to files name as following:

```
private.key
public.key
```



You can use the encryptData() method and private key that you generated to encrypt your own S3 accessKey and secretkey.

The result like following:

```
#Mon Nov 30 11:14:57 CST 2020
s3.password.accessKey=JmrCnC4h+nUb8nq8o65UCUDua7TtCTNzy5zwwsbLOvX5xCZOs/DcQSHM6yBLvO5sF1eQ2KR2BvXcPQQYUafMm/AXAJGgr1dmvGLVieo/ulLJ0Uol0ohIPM3/UO/jXh4uo6V3Rd/sdM7OwUP9CCit+wK4pY9+tQ64gS55Kh8XUAx0YMSwAlgCA6796A6fAOHIjw3Y5U7aPgPLRKFAJJIiaQbRg019eqFQJ+ihF245L7F2Hjc2t2fOWuNlpWCQ5QIjOuNvbG5b72cBkB7CCTNWLtddgQ75eTH1PNb65EDHGgLbURBYTD9HfMT3y+74OfA3MkpGIZNFm4lzb5qlX1gAEQ\=\=

s3.password.secretkey=n0rn+nam61O7c6Bz2+pAOqVCwaJjjKsczNLAggGPPh+g9Kc+knWJfhSK7cStetbmseWCTG758dDss2N02exPx6j7/4pYELyMfYQFFl0xCCdfu5ySPjSD1fqzcprAH/yQJhGAvUIonUZMB24DsrZkA8bImVZ9hxoz4wXhCC0sKif9FON+oIsic/WgLs1NuDsiFwivHq+bEMZjzLQOd9/ZL7wGd7QZmgDW18bHLyzezETXjzazjK6o0ekQ/KF/4sZLn7yUGVIuG9XEI6xeMtvd3hx/Bit55enMAk9AsnpmGZJ4BQne3eCjKbVueRKqFwMx0jDKGsE5wQe3dp8td5H7Ww\=\=
```

## 2. Configure in Singleton S3 I18n Service Build

Copy public.key file to the directory of singleton.jar 

```
ls 
singleton.jar public.key
```


Change encrypted accessKey, secretkey content  to  Singleton S3 I18n Service Build configuration (application-s3.properties)

```
#S3 store config
s3.keysEncryptEnable=true
# the public key file's path
s3.publicKey=./public.key
# the accesskey that get from above encrypt result content
s3.accessKey=#####
## the secretkey that get from above encrypt result content
s3.secretkey=######
##get from S3 configuration's region 
s3.region=###### 
##get from S3 configuration's bucket name
s3.bucketName=######
```

Start Singleton S3 I18n Service Build

```
java -jar singleton.jar
```
