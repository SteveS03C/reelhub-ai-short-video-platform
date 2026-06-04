package com.show.admin.scetc.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.show.admin.scetc.mapper.AdminUserMapper;
import com.show.admin.scetc.pojo.AdminUser;
import com.show.admin.scetc.service.AdminUserService;
import com.show.admin.scetc.utils.CommonUtils;

/**
 * 接口实现类
 * 
 * @author Ray
 *
 */
@Service
public class AdminUserServiceImp implements AdminUserService {

	@Autowired
	private AdminUserMapper adminUserMapper;

	public List<AdminUser> queryAll() {
		return adminUserMapper.selectAll();
	}

	@Override
	public AdminUser login(String username, String password) {

		AdminUser adminUser = new AdminUser();
		adminUser.setUsername(username);
		adminUser = selectOne(adminUser);
		System.out.println("查询到的用户: " + adminUser);
		if (adminUser != null) {
			System.out.println("用户salt: " + adminUser.getSalt());
			System.out.println("用户密码: " + adminUser.getPassword());
			System.out.println("输入密码: " + password);
			
			// 尝试多种密码验证方式
			// 1. 带盐值的MD5加密
			if (adminUser.getSalt() != null) {
				String encryptedPassword = CommonUtils.calculateMD5(adminUser.getSalt() + password);
				System.out.println("带盐值加密后的密码: " + encryptedPassword);
				if (adminUser.getPassword().equalsIgnoreCase(encryptedPassword)) {
					System.out.println("密码验证成功（带盐值）");
					return adminUser;
				}
			}
			
			// 2. 直接MD5加密
			String directEncryptedPassword = CommonUtils.calculateMD5(password);
			System.out.println("直接加密后的密码: " + directEncryptedPassword);
			if (adminUser.getPassword().equalsIgnoreCase(directEncryptedPassword)) {
				System.out.println("密码验证成功（直接加密）");
				return adminUser;
			}
			
			// 3. 直接比对明文密码（仅用于测试）
			if (adminUser.getPassword().equals(password)) {
				System.out.println("密码验证成功（明文）");
				return adminUser;
			}
			
			System.out.println("所有密码验证方式都失败");
		} else {
			System.out.println("用户不存在");
		}
		return null;
	}

	public AdminUser selectOne(AdminUser adminUser) {
		return adminUserMapper.selectOne(adminUser);
	}

	@Override
	public AdminUser selectOneById(Long id) {
		AdminUser adminUser = new AdminUser();
		adminUser.setId(id);
		return adminUserMapper.selectOne(adminUser);
	}

	@Override
	public boolean check(String oldPassword, AdminUser adminUser) {
		if (CommonUtils.calculateMD5(adminUser.getSalt() + oldPassword).equalsIgnoreCase(adminUser.getPassword())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void update(AdminUser adminUser) {
		adminUserMapper.updateByPrimaryKeySelective(adminUser);
	}

}



