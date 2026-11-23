/*
 * Copyright 2019-2026 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * PEM armor must be dropped before the X.509 public key is decoded, and a
 * file that is already bare base64 must still decode.
 */
public class RsaCryptUtilsTest {

	/**
	 * Bare key used by vip-manager-l10n TestModels.testRsaUtil.
	 */
	private static final String BARE_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCB2ahoumYOFtIN5W1I8NIPDQNH/wK1YQsWtxqrXAu67XEg6rCm7Lwdj889L5kXuI3+nW93833zxb5+K1W//R+srfcwA/jZMqs1EVKyrdareWqVW0b6DvZFPV38UQVJmwfBJBkdUoTSZtqBrFhSFMfFWSG7Qoxz1NlJaJylkaEk3QIDAQAB";

	@Test
	public void pemWrappedX509PublicKeyDecodes() throws Exception {
		String pem = "-----BEGIN PUBLIC KEY-----\n" + wrap(BARE_PUBLIC_KEY) + "-----END PUBLIC KEY-----\n";
		PublicKey fromPem = decode(read(pem));
		PublicKey fromBare = decode(read(BARE_PUBLIC_KEY));

		Assert.assertEquals(BARE_PUBLIC_KEY, read(pem));
		Assert.assertEquals("RSA", fromPem.getAlgorithm());
		Assert.assertArrayEquals(fromBare.getEncoded(), fromPem.getEncoded());
	}

	@Test
	public void bareBase64PublicKeyDecodes() throws Exception {
		String body = read(BARE_PUBLIC_KEY);

		Assert.assertEquals(BARE_PUBLIC_KEY, body);
		Assert.assertEquals("RSA", decode(body).getAlgorithm());
	}

	private static String read(String text) {
		return RsaCryptUtils.getPublicKeyStrFromInputStream(
				new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
	}

	private static PublicKey decode(String base64Body) throws Exception {
		byte[] encoded = Base64.decodeBase64(base64Body);
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
	}

	private static String wrap(String base64) {
		StringBuilder wrapped = new StringBuilder();
		for (int i = 0; i < base64.length(); i += 64) {
			wrapped.append(base64, i, Math.min(i + 64, base64.length()));
			wrapped.append('\n');
		}
		return wrapped.toString();
	}
}
