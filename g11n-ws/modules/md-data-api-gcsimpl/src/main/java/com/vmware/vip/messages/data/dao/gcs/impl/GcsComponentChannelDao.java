/*
 * Copyright 2019-2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.gcs.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.messages.data.dao.api.IComponentChannelDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;
import com.vmware.vip.messages.data.gcs.conf.GcsClient;
import com.vmware.vip.messages.data.gcs.conf.GcsConfig;
import com.vmware.vip.messages.data.gcs.util.GcsUtils;

@Profile("gcs")
@Repository
public class GcsComponentChannelDao implements IComponentChannelDao {
    private static Logger logger = LoggerFactory.getLogger(GcsComponentChannelDao.class);

    @Autowired
    private GcsClient gcsClient;

    @Autowired
    private GcsConfig config;

    @Override
    public List<ResultMessageChannel> getTransReadableByteChannels(String productName, String version,
            List<String> components, List<String> locales) throws DataException {
        logger.debug("GcsComponentChannelDao.getTransReadableByteChannels()-> product={}, version={}, components={}, locales={}",
                productName, version, components, locales);
        List<ResultMessageChannel> resultChannels = new ArrayList<ResultMessageChannel>();
        for (String component : components) {
            for (String locale : locales) {
                String filePath = GcsUtils.genProductVersionGcsPath(productName, version) + component + ConstantsChar.BACKSLASH
                        + ResourceFilePathGetter.getLocalizedJSONFileName(locale);
                BlobId blobId = BlobId.of(config.getBucketName(), filePath);
                Blob blob = gcsClient.getGcsStorage().get(blobId);
                if (blob != null) {
                	ReadChannel readChannel = blob.reader();
                	InputStream is = Channels.newInputStream(readChannel);
                    resultChannels.add(new ResultMessageChannel(component, locale, Channels.newChannel(is)));           
                }
            }
        }
        logger.debug("fileSize: {}", resultChannels.size());

        return resultChannels;
    }

}
