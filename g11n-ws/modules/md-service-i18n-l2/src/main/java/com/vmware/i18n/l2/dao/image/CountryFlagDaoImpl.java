/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.dao.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Repository
public class CountryFlagDaoImpl implements ICountryFlagDao {
    private static Logger logger = LoggerFactory.getLogger(CountryFlagDaoImpl.class);
    private static String basePath = ConstantsKeys.IMAGE + File.separator + ConstantsKeys.FLAGS + File.separator;

    @PostConstruct
    protected void initZipCountryFlagPattern() {

        logger.info("begin to init country flag content.");
        String sourcePath = "flag/country-flag-icons-**.zip";
        try {
            Resource[] resources = resources = new PathMatchingResourcePatternResolver()
                    .getResources(ResourceUtils.CLASSPATH_URL_PREFIX + sourcePath);
            if (resources.length == 1) {
               try (ZipInputStream zin = new ZipInputStream(resources[0].getInputStream(), Charset.forName("UTF-8"))) {
                   ZipEntry ze = null;
                   while ((ze = zin.getNextEntry()) != null) {
                       if (ze.getName().startsWith("country-flag-icons-")) {
                           String zipName = ze.getName().substring(ze.getName().indexOf(ConstantsChar.BACKSLASH));
                           if ((zipName.startsWith("/flags/3x2/") || zipName.startsWith("/flags/1x1/")) && zipName.endsWith(ConstantsFile.FILE_TYPE_SVG)) {
                               try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                                   byte[] buffer = new byte[1024];
                                   int len;
                                   while ((len = zin.read(buffer)) > 0) {
                                       bos.write(buffer, 0, len);
                                   }
                                   String content = bos.toString();
                                   writeCountryFlagJsonResult(zipName, content);
                                   writeCountryFlagResult(zipName, content, ConstantsFile.FILE_TYPE_SVG);
                               }
                           }
                       }
                       zin.closeEntry();
                   }
               }
                logger.info("Init zip country flag content successfully!");

            } else {
                logger.error("only allows one country flag source zip file");
            }
        } catch (IOException e) {
            logger.error("Init zip country flag content failure!", e);
        }

    }

    private void writeCountryFlagResult(String sourcePathStr, String fileContent, String newFileNameSuffix) throws IOException {

        String pathStr = ConstantsKeys.IMAGE + sourcePathStr;
        pathStr = pathStr.replaceAll(ConstantsChar.BACKSLASH, Matcher.quoteReplacement(File.separator));
        pathStr = pathStr.replaceAll(ConstantsFile.FILE_TYPE_SVG, newFileNameSuffix);
        File file = new File(pathStr);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.deleteOnExit();
        file.createNewFile();
        logger.info(file.getAbsolutePath());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.write(ByteBuffer.wrap(fileContent.getBytes(StandardCharsets.UTF_8)));

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }


    }
    private void writeCountryFlagJsonResult(String sourcePathStr, String flagContent) throws IOException {
        int regionIdx = sourcePathStr.lastIndexOf(ConstantsChar.BACKSLASH)+1;
        String region = sourcePathStr.substring(regionIdx).replace(ConstantsFile.FILE_TYPE_SVG, "");

        Map<String, String> respData = new HashMap<>();
        respData.put("type", "svg");
        respData.put("image", flagContent);
        respData.put("region", region);
        APIResponseDTO apiResponseDTO = new APIResponseDTO();
        apiResponseDTO.setData(respData);
        apiResponseDTO.setResponse(APIResponseStatus.OK);
        try {
               String content = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(apiResponseDTO);
               writeCountryFlagResult(sourcePathStr, content, ConstantsFile.FILE_TPYE_JSON);
        } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                throw e;
        }

    }


    @Override
    public FileChannel getCountryFlagChannel(String scale, String shortName, String fileNameSuffix) throws Exception {

        StringBuilder sourcePath = new StringBuilder(basePath);
        sourcePath.append(scale.replaceAll("\\.", "").replaceAll("/", "")).append(File.separator);
        sourcePath.append(shortName.replaceAll("\\.", "").replaceAll("/", "")).append(fileNameSuffix);
        return new FileInputStream(new File(sourcePath.toString())).getChannel();

    }
}
