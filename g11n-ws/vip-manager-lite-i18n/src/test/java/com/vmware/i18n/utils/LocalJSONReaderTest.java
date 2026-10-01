/*
 * Copyright 2026 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocalJSONReaderTest {

	private static final String FILE = "cldr/pattern/common/parse.json";

	@Test
	public void testNestedJarUrl() {
		assertEquals("jar:nested:/opt/app.jar/!BOOT-INF/lib/x.jar!/" + FILE,
				LocalJSONReader.toJarUrl("nested:/opt/app.jar/!BOOT-INF/lib/x.jar!/", FILE));
	}

	@Test
	public void testNestedJarUrlWithoutTrailingSeparator() {
		assertEquals("jar:nested:/opt/app.jar/!BOOT-INF/lib/x.jar!/" + FILE,
				LocalJSONReader.toJarUrl("nested:/opt/app.jar/!BOOT-INF/lib/x.jar", FILE));
	}

	@Test
	public void testClassicLoaderJarUrl() {
		assertEquals("jar:file:/opt/app.jar!/BOOT-INF/lib/x.jar!/" + FILE,
				LocalJSONReader.toJarUrl("file:/opt/app.jar!/BOOT-INF/lib/x.jar!/", FILE));
	}

	@Test
	public void testPlainJarUrl() {
		assertEquals("jar:file:/home/u/.gradle/x.jar!/" + FILE,
				LocalJSONReader.toJarUrl("/home/u/.gradle/x.jar", FILE));
	}
}
