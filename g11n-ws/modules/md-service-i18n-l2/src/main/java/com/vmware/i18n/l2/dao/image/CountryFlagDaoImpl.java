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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CountryFlagDaoImpl implements ICountryFlagDao {
    private static Logger logger = LoggerFactory.getLogger(CountryFlagDaoImpl.class);
    private static String basePath = ConstantsKeys.IMAGE+File.separator+ConstantsKeys.FLAGS+File.separator;


    @PostConstruct
    protected void initCountryFlagPattern()  {
        logger.info("begin to init country flag content.");
        String sourcePath = "flags/**/**.svg";
        try {
            Resource[] resources  = resources = new PathMatchingResourcePatternResolver()
                .getResources(ResourceUtils.CLASSPATH_URL_PREFIX+sourcePath);

            for(Resource resource: resources) {
                String pathStr = resource.getURL().getFile();

                int index = pathStr.lastIndexOf("!/flags/")+1;
                pathStr = ConstantsKeys.IMAGE+pathStr.substring(index);
                logger.info(pathStr);
                pathStr = pathStr.replaceAll(ConstantsChar.BACKSLASH, File.separator);
                pathStr = pathStr.replaceAll(ConstantsFile.FILE_TYPE_SVG, ConstantsFile.FILE_TPYE_JSON);
                File file = new File(pathStr);
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.deleteOnExit();
                file.createNewFile();
                String region = file.getName().replace(ConstantsFile.FILE_TPYE_JSON,"");

                StringBuilder sb = new StringBuilder();
                try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()))){
                    String temp = null;
                    while((temp = bufferedReader.readLine()) != null){
                        sb.append(temp).append("\n");
                    }
                }
                Map<String,String> respData = new HashMap<>();
                respData.put("type", "svg");
                respData.put("image", sb.toString());
                respData.put("region", region);
                APIResponseDTO apiResponseDTO = new APIResponseDTO();
                apiResponseDTO.setData(respData);
                apiResponseDTO.setResponse(APIResponseStatus.OK);

                try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                     FileChannel fileChannel = fileOutputStream.getChannel() ) {
                    String content = null;
                    try {
                        content = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(apiResponseDTO);
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage(), e);
                        throw e;
                    }
                    fileChannel.write(ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8)));

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

        StringBuilder sourcePath = new StringBuilder(basePath);
        sourcePath.append(scale.replaceAll("\\.", "").replaceAll("/", "")).append(File.separator);
        sourcePath.append(shortName.replaceAll("\\.", "").replaceAll("/", "")).append(ConstantsFile.FILE_TPYE_JSON);
        return new FileInputStream(new File(sourcePath.toString())).getChannel();

    }
}
