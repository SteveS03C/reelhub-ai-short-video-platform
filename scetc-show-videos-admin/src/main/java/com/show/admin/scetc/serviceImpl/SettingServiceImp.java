package com.show.admin.scetc.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.show.admin.scetc.mapper.SettingMapper;
import com.show.admin.scetc.pojo.Setting;
import com.show.admin.scetc.service.SettingService;

/**
 * 接口实现类
 * 
 * @author Ray
 */
@Service
public class SettingServiceImp implements SettingService {

	@Autowired
	private SettingMapper settingMapper;

	@Override
	public String getValueByName(String name) {
		Setting setting = new Setting();
		setting.setName(name);
		setting = settingMapper.selectOne(setting);
		return setting == null ? null : setting.getValue();
	}

	@Override
	public void saveValueByName(String name, String value) {
		Setting query = new Setting();
		query.setName(name);
		Setting setting = settingMapper.selectOne(query);
		if (setting == null) {
			setting = new Setting();
			if ("email_smtp_host".equals(name)) {
				setting.setId(1L);
			} else if ("email_smtp_username".equals(name)) {
				setting.setId(2L);
			} else if ("email_smtp_password".equals(name)) {
				setting.setId(3L);
			} else if ("email_from".equals(name)) {
				setting.setId(4L);
			} else {
				setting.setId(System.currentTimeMillis());
			}
			setting.setName(name);
			setting.setValue(value);
			setting.setIsDeleted(false);
			settingMapper.insert(setting);
			return;
		}
		setting.setValue(value);
		settingMapper.updateByPrimaryKeySelective(setting);
	}

}



