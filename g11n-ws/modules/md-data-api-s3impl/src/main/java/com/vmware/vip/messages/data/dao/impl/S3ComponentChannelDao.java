package com.vmware.vip.messages.data.dao.impl;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.messages.data.conf.S3Client;
import com.vmware.vip.messages.data.conf.S3Config;
import com.vmware.vip.messages.data.dao.api.IComponentChannelDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.util.S3Utils;

@Profile("s3")
@Repository
public class S3ComponentChannelDao implements IComponentChannelDao {
	private static Logger logger = LoggerFactory.getLogger(S3ComponentChannelDao.class);

	@Autowired
	private S3ProductDaoImpl productDao;

	@Autowired
	private S3Client s3Client;

	@Autowired
	private S3Config config;

	@Override
	public List<ReadableByteChannel> getTransReadableByteChannels(String productName, String version,
			List<String> components, List<String> locales) throws DataException {

		List<ReadableByteChannel> resultChannels = new ArrayList<ReadableByteChannel>();
		for (String component : components) {
			for (String locale : locales) {
				String filePath = S3Utils.genProductVersionS3Path(productName, version) + component + ConstantsChar.BACKSLASH
						+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);
				if (s3Client.getS3Client().doesObjectExist(config.getBucketName(), filePath)) {
					S3Object s3Obj = s3Client.getS3Client().getObject(config.getBucketName(), filePath);
					if (s3Obj != null) {
						S3ObjectInputStream s3is = s3Obj.getObjectContent();
						 resultChannels.add(Channels.newChannel(s3is));
					} 
					
				}
               
			}
		}
		logger.info("fileSize: {}", resultChannels.size());

		return resultChannels;
	}

	@Override
	public ReadableByteChannel getTransReadableByteChannel(String productName, String version, String component,
			String locale) throws DataException {
		String filePath = S3Utils.genProductVersionS3Path(productName, version) + component + ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter.getLocalizedJSONFileName(locale);
		if (s3Client.getS3Client().doesObjectExist(config.getBucketName(), filePath)) {
			S3Object s3Obj = s3Client.getS3Client().getObject(config.getBucketName(), filePath);
			if (s3Obj != null) {
				S3ObjectInputStream s3is = s3Obj.getObjectContent();
				return Channels.newChannel(s3is);
			} else {
				throw new DataException(S3OneComponentDaoImpl.S3_NOT_EXIST_STR + filePath);
			}
		}else {
			throw new DataException(S3OneComponentDaoImpl.S3_NOT_EXIST_STR + filePath);
		}
	}


}
