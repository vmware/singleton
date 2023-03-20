/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vmware.vip.common.constants.ConstantsTomcat;

/**
 * this is used to the Tomcat Configuration
 *
 */
@Configuration
public class TomcatConfig {

	@Value("${config.gzip.enable}")
	private String compression;

	@Value("${config.gzip.minsize}")
	private int compressionMinSize;

	@Bean
	public ServletWebServerFactory servletContainer(ServerProperties serverProperties) {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addConnectorCustomizers(new VIPTomcatConnectionCustomizer(serverProperties, compression, compressionMinSize));
		if (serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTP_HTTPS) ||
				serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTPS_HTTP)) {
			tomcat.addAdditionalTomcatConnectors(initiateHttpsConnector(serverProperties));
		}
		return tomcat;
	}

	/**
	 * create the https additional connection for tomcat
	 */
	private Connector initiateHttpsConnector(ServerProperties serverProperties) {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme(ConstantsTomcat.HTTPS);
		connector.setPort(serverProperties.getServerPort());
		connector.setSecure(true);
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
		protocol.setSSLEnabled(true);
		protocol.setKeystoreFile(serverProperties.getHttpsKeyStore());
		protocol.setKeystorePass(serverProperties.getHttpsKeyStorePassword());
		protocol.setKeystoreType(serverProperties.getHttpsKeyStoreType());
		protocol.setKeyPass(serverProperties.getHttpsKeyPassword());
		protocol.setKeyAlias(serverProperties.getHttpsKeyAlias());
		protocol.setMaxHttpHeaderSize(serverProperties.getMaxHttpHeaderSize());
		connector.setRedirectPort(ConstantsTomcat.REDIRECT_PORT);
		connector.setAllowTrace(serverProperties.isAllowTrace());
		protocol.setCompression(compression);
		protocol.setCompressionMinSize(compressionMinSize);
		return connector;
	}

}
