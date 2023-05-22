/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.dao.pattern;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

@Repository
public class CountryFlagDaoImpl implements ICountryFlagDao{
    private Logger logger = LoggerFactory.getLogger(CountryFlagDaoImpl.class);

    @PostConstruct
    private void initCountryFlagPattern()  {
        logger.info("begin to init country flag content.");
        String sourcePath = "flags/**/**.svg";
        try {
            Resource[] resources  = resources = new PathMatchingResourcePatternResolver()
                .getResources(ResourceUtils.CLASSPATH_URL_PREFIX+sourcePath);

            for(Resource resource: resources) {
                String pathStr = resource.getURL().getFile();

                int index = pathStr.lastIndexOf("!/flags/")+1;
                pathStr = ConstantsKeys.PATTERN+pathStr.substring(index);
                logger.info(pathStr);
                pathStr = pathStr.replaceAll(ConstantsChar.BACKSLASH, File.separator);
                File file = new File(pathStr);
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.deleteOnExit();
                file.createNewFile();
                try (ReadableByteChannel readableByteChannel = Channels.newChannel(resource.getInputStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(file);
                     FileChannel fileChannel = fileOutputStream.getChannel() ) {

                    fileChannel.transferFrom(readableByteChannel, 0, resource.contentLength());

                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw e;
                }

            }

        } catch (IOException e) {
            logger.error("Init the country flag content failure!", e);
        }
        logger.info("Init country flag content successfully!");

    }

    @Override
    public FileChannel getCountryFlagChannel(String scale, String shortName) throws Exception{
        StringBuilder sourcePath = new StringBuilder();
        sourcePath.append(ConstantsKeys.PATTERN).append(File.separator);
        sourcePath.append("flags").append(File.separator);
        sourcePath.append(scale).append(File.separator);
        sourcePath.append(shortName).append(".svg");
        return new FileInputStream(new File(sourcePath.toString())).getChannel();

    }
}
