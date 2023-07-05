/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
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
public class LiteTomcatConfig {

	@Value("${config.gzip.enable:off}")
	private String compression;

	@Value("${config.gzip.minsize:2048}")
	private int compressionMinSize;

	@Bean
	public ServletWebServerFactory servletContainer(LiteServerProperties serverProperties) {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addConnectorCustomizers(new LiteTomcatConnectionCustomizer(serverProperties, compression, compressionMinSize));
		if (serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTP_HTTPS) ||
				serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTPS_HTTP)) {
			tomcat.addAdditionalTomcatConnectors(initiateHttpsConnector(serverProperties));
		}
		return tomcat;
	}

	/**
	 * create the https additional connection for tomcat
	 */
	private Connector initiateHttpsConnector(LiteServerProperties serverProperties) {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme(ConstantsTomcat.HTTPS);
		connector.setPort(serverProperties.getServerPort());
		connector.setSecure(true);
		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
		protocol.setSSLEnabled(true);

		SSLHostConfig sslHostConfig = new SSLHostConfig();
		SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.RSA);
		certificate.setCertificateKeystoreFile(serverProperties.getHttpsKeyStore());
		certificate.setCertificateKeystorePassword(serverProperties.getHttpsKeyStorePassword());
		certificate.setCertificateKeystoreType(serverProperties.getHttpsKeyStoreType());
		certificate.setCertificateKeyPassword(serverProperties.getHttpsKeyPassword());
		certificate.setCertificateKeyAlias(serverProperties.getHttpsKeyAlias());
		sslHostConfig.addCertificate(certificate);

		protocol.addSslHostConfig(sslHostConfig);
		protocol.setMaxHttpHeaderSize(serverProperties.getMaxHttpHeaderSize());
		connector.setRedirectPort(ConstantsTomcat.REDIRECT_PORT);
		connector.setAllowTrace(serverProperties.isAllowTrace());
		protocol.setCompression(compression);
		protocol.setCompressionMinSize(compressionMinSize);
		return connector;
	}

}
