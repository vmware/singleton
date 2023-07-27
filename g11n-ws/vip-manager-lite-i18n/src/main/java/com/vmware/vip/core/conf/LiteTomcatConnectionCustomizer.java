/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.conf;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;

import com.vmware.vip.common.constants.ConstantsTomcat;

/**
 * 
 * this is custom tomcat Connector
 *
 */
public class LiteTomcatConnectionCustomizer implements TomcatConnectorCustomizer {
	private static Logger logger = LoggerFactory.getLogger(LiteTomcatConnectionCustomizer.class);
	private LiteServerProperties serverProperties;

	private String compression;

	private int compressionMinSize;

	public LiteTomcatConnectionCustomizer(LiteServerProperties prop, String compression, int compressionMinSize) {
		this.serverProperties = prop;
		this.compression = compression;
		this.compressionMinSize = compressionMinSize;
	}

	@Override
	public void customize(Connector connector) {
		
		if (this.serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTP_HTTPS) ||
				this.serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTPS_HTTP)) {
			logger.info("the tomcat support http and https protocol");
			connector.setPort(serverProperties.getHttpPort());

			connector.setProperty("protocol", ConstantsTomcat.HTTP);
			connector.setRedirectPort( ConstantsTomcat.REDIRECT_PORT);

			connector.setAllowTrace(serverProperties.isAllowTrace());
			Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
			protocol.setMaxHttpHeaderSize(serverProperties.getMaxHttpHeaderSize());
			protocol.setCompression(compression);
			protocol.setCompressionMinSize(compressionMinSize);
		} else if(serverProperties.getServerScheme().equalsIgnoreCase(ConstantsTomcat.HTTP)){
			logger.info("the tomcat only support http protocol");
			connector.setPort(serverProperties.getHttpPort());

			connector.setProperty("protocol", ConstantsTomcat.HTTP);
			connector.setRedirectPort( ConstantsTomcat.REDIRECT_PORT);

			connector.setAllowTrace(serverProperties.isAllowTrace());
			Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
			protocol.setMaxHttpHeaderSize(serverProperties.getMaxHttpHeaderSize());
			protocol.setCompression(compression);
			protocol.setCompressionMinSize(compressionMinSize);
		}else{
			logger.info("the tomcat only support https protocol");
			connector.setScheme(ConstantsTomcat.HTTPS);
			connector.setPort(serverProperties.getServerPort());
			connector.setSecure(true);
			Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
			protocol.setSSLEnabled(true);

			SSLHostConfig sslHostConfig = new SSLHostConfig();
			SSLHostConfigCertificate certificate = new SSLHostConfigCertificate();
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

		}

		connector.setAllowBackslash(true);
		connector.setEncodedSolidusHandling(EncodedSolidusHandling.DECODE.getValue());

	}
}

