/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MTConfigContent {
	@Value("${mt.server:https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=}")
	private String mtServer;

	@Value("${mt.key:######}")
	private String key;

	@Value("${mt.translatedMax:500}")
	private String translatedMax;

	@Value("${mt.translatedCount:20}")
	private String translatedCount;

	@Value("${mt.region:}")
	private String region;

	@PostConstruct
	public void initConfig() {
		MTConfig.setMTSERVER(this.getMtServer());
		MTConfig.setKEY(this.getKey());
		MTConfig.setTRANSLATED_MAX(this.getTranslatedMax());
		MTConfig.setTRANSLATECOUNT(this.getTranslatedCount());
		MTConfig.setREGION(this.region);
	}

	public String getMtServer() {
		return mtServer;
	}

	public String getKey() {
		return key;
	}

	public String getTranslatedMax() {
		return translatedMax;
	}

	public String getTranslatedCount() {
		return translatedCount;
	}

}
