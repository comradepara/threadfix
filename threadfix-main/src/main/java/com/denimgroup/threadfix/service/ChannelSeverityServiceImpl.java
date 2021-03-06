////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.denimgroup.threadfix.data.dao.ChannelSeverityDao;
import com.denimgroup.threadfix.data.dao.ChannelTypeDao;
import com.denimgroup.threadfix.data.entities.ChannelSeverity;

@Service
@Transactional(readOnly = true)
public class ChannelSeverityServiceImpl implements ChannelSeverityService {

	private ChannelSeverityDao channelSeverityDao;
	private ChannelTypeDao channelTypeDao;

	@Autowired
	public ChannelSeverityServiceImpl(ChannelTypeDao channelTypeDao,
			ChannelSeverityDao channelSeverityDao) {
		this.channelSeverityDao = channelSeverityDao;
		this.channelTypeDao = channelTypeDao;
	}

	@Override
	public List<ChannelSeverity> loadByChannel(String channelTypeName) {
		return channelSeverityDao.retrieveByChannel(
				channelTypeDao.retrieveByName(channelTypeName));
	}
	
	@Override
	public ChannelSeverity loadById(int id) {
		return channelSeverityDao.retrieveById(id);
	}

}
