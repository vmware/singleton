/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import static org.asciidoctor.Asciidoctor.Factory.create;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.vmware.vip.BootApplication;
import com.vmware.vip.core.conf.SwaggerConfig;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureRestDocs(outputDir = "build/asciidoc/snippets")
@SpringBootTest(classes = { BootApplication.class, SwaggerConfig.class })
@AutoConfigureMockMvc
public class Swagger2ToDocTest {

	private String baseOutputDir = "src/docs/asciidoc";
	private String outputDirV1 = "src/docs/asciidoc/generated/v1";
	private String outputDirV2 = "src/docs/asciidoc/generated/v2";
	private String groupPath = "/i18n/api/v2/api-docs?group=";

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void generateAsciiDoc() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get(this.groupPath + "v1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse response = mvcResult.getResponse();
		Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder().withFlatBody().withListDelimiter()
				.withoutInlineSchema().withInterDocumentCrossReferences().withGeneratedExamples()
				.withPathsGroupedBy(GroupBy.TAGS).build();

		String swaggerJsonV1 = response.getContentAsString();
		Swagger2MarkupConverter.from(swaggerJsonV1).withConfig(config).build().toFolder(Paths.get(outputDirV1));

		mvcResult = this.mockMvc.perform(get(this.groupPath + "v2").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		response = mvcResult.getResponse();
		String swaggerJsonV2 = response.getContentAsString();

		Swagger2MarkupConverter.from(swaggerJsonV2).withConfig(config).build().toFolder(Paths.get(outputDirV2));
	}

	@Test
	public void convertV1Doc() {
		Asciidoctor asciidoctor = create();
		Options options = new Options();
		options.setInPlace(true);
		options.setBackend("html5");
		options.setSafe(SafeMode.SAFE);
		options.setToDir(baseOutputDir + "/html5/v1");
		options.setMkDirs(true);
		options.setDocType("docbook");

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("toc", "left");
		attributes.put("toclevels", "3");
		attributes.put("generated", "./generated/v1");
		options.setAttributes(attributes);

		asciidoctor.convertFile(new File(baseOutputDir + "/index.adoc"), options);
	}

	@Test
	public void convertV2Doc() {
		Asciidoctor asciidoctor = create();
		Options options = new Options();
		options.setInPlace(true);
		options.setBackend("html5");
		options.setSafe(SafeMode.SAFE);
		options.setToDir(baseOutputDir + "/html5/v2");
		options.setMkDirs(true);
		options.setDocType("docbook");

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("toc", "left");
		attributes.put("toclevels", "3");
		attributes.put("generated", "./generated/v2");
		options.setAttributes(attributes);

		asciidoctor.convertFile(new File(baseOutputDir + "/index.adoc"), options);
	}

}
