package com.vmware.vip.messages.data.dao.pgimpl;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vmware.vip.messages.data.dao.api.IComponentChannelDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;
import com.vmware.vip.messages.data.dao.pgimpl.balance.PgDataNodeBalancerAdapter;
import com.vmware.vip.messages.data.dao.pgimpl.operate.IDocOperate;

@Repository
public class PgComponentChannelDao implements IComponentChannelDao {
	private static Logger logger = LoggerFactory.getLogger(PgComponentChannelDao.class);

	@Autowired
	private PgOneComponentApiImpl pgOneComponentApiImpl;

	@Autowired
	private PgDataNodeBalancerAdapter datanodes;

	@Autowired
	private IDocOperate docOperate;

	@Override
	public List<ResultMessageChannel> getTransReadableByteChannels(String productName, String version,
			List<String> components, List<String> locales) throws DataException {
		List<ResultMessageChannel> resultList = new ArrayList<>();
		for (String comp : components) {
			for (String locale : locales) {
				String result = docOperate.findByDocId(productName, version, comp, locale,
						datanodes.getDataNodeByProduct(productName));
				;
				if (result != null) {
					ByteArrayInputStream stringInputStream = new ByteArrayInputStream(result.getBytes());
					resultList.add(new ResultMessageChannel(comp, locale, Channels.newChannel(stringInputStream)));
				} else {
					String warnMsg = String.format("%s--%s--%s---%s-- query no data in DB", productName, version, comp,
							locale);
					logger.warn(warnMsg);
				}
			}
		}

		if (resultList.size() == 0) {
			throw new DataException("this no component in DB return json");
		}

		logger.info("Message Size: {}", resultList.size());
		return resultList;
	}

	@Override
	public ReadableByteChannel getTransReadableByteChannel(String productName, String version, String component,
			String locale) throws DataException {
		String resultStr = pgOneComponentApiImpl.get2JsonStr(productName, version, component, locale);
		ByteArrayInputStream stringInputStream = new ByteArrayInputStream(resultStr.getBytes());
		return Channels.newChannel(stringInputStream);
	}

}
