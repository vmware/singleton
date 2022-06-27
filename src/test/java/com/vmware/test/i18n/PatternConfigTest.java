package com.vmware.test.i18n;

import org.junit.Assert;
import org.junit.Test;

import com.vmware.i18n.PatternConfig;

public class PatternConfigTest {
	@Test
	public void testPatternPath(){
		String pathStr = "test";
		PatternConfig config = PatternConfig.getInstance();
		config.setPatternPath(pathStr);
		
		Assert.assertSame(pathStr, config.getPatternPath());
	}

}
