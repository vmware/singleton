/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.IMultComponentService;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StreamProductAction extends TranslationProductAction {
    private static byte[] byteComm = (ConstantsChar.COMMA + "\r\n").getBytes();

    @Autowired
    IProductService productService;

    @Autowired
    IMultComponentService multComponentService;


    public void writeMultTranslationResponse(String productName, String version, String components, String locales,
                                             String pseudo, HttpServletResponse resp) throws Exception {

        String newVersion = super.getAvailableVersion(productName, version);
        List<String> componentList = getcomponentList(productName, newVersion, components);
        List<String> localeList = getlocaleList(productName, newVersion, locales, pseudo);

        if (Boolean.valueOf(pseudo)){
            writePseudoTranslationsToChannel(productName, newVersion, componentList, localeList, version, resp);
            return;
        }

        List<ResultMessageChannel> readChannels = multComponentService.getTranslationChannels(productName, newVersion, componentList, localeList);
        if (readChannels.isEmpty()) {
            throw new L3APIException(ConstantsMsg.TRANS_IS_NOT_FOUND);
        }

        StreamProductResp sr = new StreamProductResp(productName, newVersion, localeList, componentList, pseudo, false);
        resp.setContentType(ConstantsKeys.CONTENT_TYPE_JSON);
        WritableByteChannel wbc = Channels.newChannel(resp.getOutputStream());

        boolean isPartContent = writeResponseHeader(version.equals(newVersion), componentList.size() * localeList.size(), readChannels.size(), wbc, sr);
        writeTranslationsToChannel(readChannels, wbc);
        if (isPartContent) {
            wbc.write(ByteBuffer.wrap(addNullMessage(readChannels, componentList, localeList)));
        }
        wbc.write(sr.getEndBytes());
    }

    /**
     * write the response header bytes to channel
     */
    private boolean writeResponseHeader(boolean notVersionFallback, int expSize, int readChannelSize, WritableByteChannel wbc, StreamProductResp sr) throws IOException {
        boolean isPartContent = false;
        if (notVersionFallback) {
            isPartContent = (readChannelSize != expSize);
            if (isPartContent) {
                wbc.write(sr.getRespStartBytes(APIResponseStatus.MULTTRANSLATION_PART_CONTENT.getCode()));
            } else {
                wbc.write(sr.getRespStartBytes(APIResponseStatus.OK.getCode()));
            }
        } else {
            wbc.write(sr.getRespStartBytes(APIResponseStatus.VERSION_FALLBACK_TRANSLATION.getCode()));
        }
        return isPartContent;
    }


    private List<String> getcomponentList(String productName, String versionStr, String components) throws L3APIException {
        List<String> componentList = null;
        if (StringUtils.isEmpty(components)) {
            componentList = productService.getComponentNameList(productName, versionStr);
        } else {
            componentList = new ArrayList<String>();
            for (String component : components.split(",")) {
                componentList.add(component.trim());
            }
        }

        return componentList;
    }

    private List<String> getlocaleList(String productName, String versionStr, String locales, String pseudo) throws L3APIException {
        List<String> localeList = new ArrayList<String>();
        if (Boolean.valueOf(pseudo)) {
            localeList.add(ConstantsKeys.LATEST);
        } else if (!StringUtils.isEmpty(locales)) {
            for (String locale : locales.split(",")) {
                localeList.add(super.getMappingLocale(productName, versionStr, locale.trim()));
            }
        } else {
            localeList = productService.getSupportedLocaleList(productName, versionStr);
        }

        return localeList;
    }

    private byte[] addNullMessage(List<ResultMessageChannel> readChannels, List<String> components, List<String> locales) {
        StringBuilder sb = new StringBuilder();
        for (String component : components) {
            for (String locale : locales) {
                ResultMessageChannel rmc = new ResultMessageChannel(component, locale, null);
                if (!readChannels.contains(rmc)) {
                    sb.append(",");
                    sb.append(rmc.generateNullMessage());
                }

            }
        }
        return sb.toString().getBytes();
    }


    private void transferTo(ReadableByteChannel readChannel, WritableByteChannel writeChannel, ByteBuffer buf) throws Exception {

        if (readChannel instanceof FileChannel) {
            try (FileChannel fileChannel = (FileChannel) readChannel) {
                fileChannel.transferTo(0, fileChannel.size(), writeChannel);
            }
        } else {

            try {
                while ((readChannel.read(buf)) != -1) {
                    ((Buffer) buf).flip();
                    do {
                        writeChannel.write(buf);
                    } while (buf.hasRemaining());

                    ((Buffer) buf).clear();
                }
            } catch (Exception e) {
                throw e;
            } finally {
                readChannel.close();
            }
        }
    }

    private void writeTranslationsToChannel(List<ResultMessageChannel> readChannels, WritableByteChannel wbc) throws Exception {
        ByteBuffer buf = null;
        ReadableByteChannel firstReadChannel = readChannels.get(0).getReadableByteChannel();
        boolean flag = (firstReadChannel instanceof FileChannel);
        if (flag) {
            transferTo(readChannels.get(0).getReadableByteChannel(), wbc, null);
            buf = ByteBuffer.wrap(byteComm);
        } else {
            buf = ByteBuffer.allocateDirect(16384);
            transferTo(readChannels.get(0).getReadableByteChannel(), wbc, buf);
        }

        for (int idx = 1; idx < readChannels.size(); idx++) {
            if (flag) {
                ((Buffer) buf).rewind();
                wbc.write(buf);
            } else {
                buf.put(byteComm);
            }
            transferTo(readChannels.get(idx).getReadableByteChannel(), wbc, buf);
        }
    }

    private void writePseudoTranslationsToChannel(String productName, String versionStr, List<String> components, List<String> locales, String reqVersion, HttpServletResponse resp) throws L3APIException, IOException {
        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setPseudo(true);
        translationDTO.setProductName(productName);
        translationDTO.setVersion(versionStr);
        translationDTO.setComponents(components);
        translationDTO.setLocales(locales);
        JSONArray result = multComponentService.getMultiComponentsTranslation(translationDTO).getBundles();

        StreamProductResp sr = new StreamProductResp(productName, versionStr, locales, components, "true", false);
        resp.setContentType(ConstantsKeys.CONTENT_TYPE_JSON);
        WritableByteChannel wbc = Channels.newChannel(resp.getOutputStream());

        boolean isPartContent = writeResponseHeader(reqVersion.equals(versionStr), components.size() * locales.size(), result.size(), wbc, sr);
        List<JSONObject> resultList = formatResultBundles(components, locales, result, isPartContent);
        wbc.write(ByteBuffer.wrap(resultList.get(0).toJSONString().getBytes()));
        ByteBuffer buf = ByteBuffer.wrap(byteComm);
        for (int i =1; i<resultList.size(); i++){
            wbc.write(buf);
            wbc.write(ByteBuffer.wrap(resultList.get(0).toJSONString().getBytes()));
            ((Buffer) buf).rewind();
        }
        wbc.write(sr.getEndBytes());
    }

    private List<JSONObject> formatResultBundles(List<String> components, List<String> locales, JSONArray result, boolean isPartContent){
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        if (isPartContent){
            for (String component : components) {
                for (String locale : locales) {
                    resultList.add(addNullBundle(component, locale, result));
                }
            }
        }else {
            Iterator<JSONObject> objectIterator =  result.iterator();
            while(objectIterator.hasNext()) {
                resultList.add(objectIterator.next());
            }
        }
        return resultList;
    }
    private JSONObject addNullBundle(String component, String locale, JSONArray result){

        Iterator<JSONObject> objectIterator =  result.iterator();
        while(objectIterator.hasNext()) {
            JSONObject object = objectIterator.next();
            String fileLocale = (String) object.get(ConstantsKeys.lOCALE);
            String fileComponent = (String) object.get(ConstantsKeys.COMPONENT);
            if(locale.equals(fileLocale)&& component.equals(fileComponent)) {
                return object;
            }
        }
        JSONObject nullObj = new JSONObject();
        nullObj.put("locale", locale);
        nullObj.put("component", component);
        nullObj.put("messages", null);
        return nullObj;
    }

}