/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.vmware.l10n.conf.S3Client;
import com.vmware.l10n.source.dao.AllowListDao;
import com.vmware.l10n.utils.AllowListUtils;
@Repository
@Profile("s3")
public class S3AllowListDaoImpl implements AllowListDao{
	
	@Value("${allow.list.location:bundle.json}")
	protected String allowlistLocation;
	
	@Autowired
	public S3Client s3Client;

	
	private String readS3AllowlistFile() {
		if (s3Client.isObjectExist(allowlistLocation)) {
			return s3Client.readObject(allowlistLocation);
		}
		return "";
	}

	@Override
	public Map<String, List<String>> getAllowList() {
		 String jsonStr = readS3AllowlistFile();
		return AllowListUtils.parseAllowList(jsonStr);
	}

}
