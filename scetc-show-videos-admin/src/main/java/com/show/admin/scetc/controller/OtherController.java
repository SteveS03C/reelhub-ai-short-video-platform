package com.show.admin.scetc.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.show.admin.scetc.pojo.AdminUser;
import com.show.admin.scetc.pojo.MailHistory;
import com.show.admin.scetc.service.MailHistoryService;
import com.show.admin.scetc.service.SettingService;
import com.show.admin.scetc.utils.EmailUtils;
import com.show.admin.scetc.utils.ImageCodeUtils;
import com.show.admin.scetc.utils.XyfJsonResult;

/**
 * @author Ray
 */
@RestController
@RequestMapping("other")
public class OtherController extends BasicController {

	private static final String email_smtp_host = "email_smtp_host";
	private static final String email_smtp_username = "email_smtp_username";
	private static final String email_smtp_password = "email_smtp_password";
	private static final String email_from = "email_from";

	@Autowired
	private SettingService settingService;// 配置文件
	
	@Autowired
	private MailHistoryService mailHistoryService;// 邮件历史记录服务

	/**
	 * 图片验证码
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping("/imageCode.do")
	public void sendImageCode(HttpServletResponse response, HttpServletRequest request) {
		ImageCodeUtils.sendImageCode(request.getSession(), response);
	}

	/**
	 * 发送html格式的邮件
	 * 
	 * @return
	 */
	@PostMapping("/sendEmail.do")
	public XyfJsonResult sendEmail(String to, String subject, String content, HttpServletRequest request) {
		System.out.println("====== [DEBUG] 收到发信请求 ======");
		System.out.println("to: " + to);
		System.out.println("subject: " + subject);
		System.out.println("content: " + content);
		
		if (content != null && content.contains("[object Object]")) {
			return XyfJsonResult.errorMsg("前端内容格式错误，拦截[object Object]");
		}
		
		String smtpServer = settingService.getValueByName(email_smtp_host);
		String smtpUsername = settingService.getValueByName(email_smtp_username);
		String smtpPassword = settingService.getValueByName(email_smtp_password);
		String smtpFrom = settingService.getValueByName(email_from);
		if (isBlank(to) || isBlank(subject) || isBlank(content)) {
			return XyfJsonResult.errorMsg("收件人、主题、内容不能为空");
		}
		if (isBlank(smtpServer) || isBlank(smtpUsername) || isBlank(smtpPassword) || isBlank(smtpFrom)) {
			return XyfJsonResult.errorMsg("邮件配置不完整，请在setting表中配置SMTP参数");
		}
		AdminUser adminUserVo = (AdminUser) request.getSession().getAttribute("adminUser");
		
		try {
			EmailUtils.sendHtmlMail(smtpServer, smtpUsername, smtpPassword, smtpFrom, to, subject, content);
			
			MailHistory mailHistory = new MailHistory();
			mailHistory.setSendTime(new Date());
			mailHistory.setFromEmail(smtpFrom);
			mailHistory.setToEmail(to);
			mailHistory.setSubject(subject);
			mailHistory.setContent(content);
			mailHistory.setStatus("成功");
			mailHistory.setFolderType("SENT");
			mailHistory.setDeletedFlag(0);
			mailHistory.setAdminUsername(adminUserVo == null ? null : adminUserVo.getUsername());
			mailHistoryService.save(mailHistory);
			
			if (adminUserVo != null) {
				SimpleDateFormat formate = new SimpleDateFormat();
				String date = formate.format(new Date());
				redis.lpush(Operate_REDIS_SESSION, date + "&nbsp;&nbsp;&nbsp;" + adminUserVo.getRealName() + ":发送邮件到 " + to);
			}
			
			return XyfJsonResult.ok();
		} catch (Exception e) {
			MailHistory mailHistory = new MailHistory();
			mailHistory.setSendTime(new Date());
			mailHistory.setFromEmail(smtpFrom);
			mailHistory.setToEmail(to);
			mailHistory.setSubject(subject);
			mailHistory.setContent(content);
			mailHistory.setStatus("失败: " + e.getMessage());
			mailHistory.setFolderType("SENT");
			mailHistory.setDeletedFlag(0);
			mailHistory.setAdminUsername(adminUserVo == null ? null : adminUserVo.getUsername());
			mailHistoryService.save(mailHistory);
			e.printStackTrace();
			return XyfJsonResult.errorMsg("邮件发送失败: " + e.getMessage());
		}
	}

	@GetMapping("/getEmailConfig.do")
	public XyfJsonResult getEmailConfig() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("smtpServer", defaultString(settingService.getValueByName(email_smtp_host)));
		data.put("smtpUsername", defaultString(settingService.getValueByName(email_smtp_username)));
		data.put("smtpPassword", defaultString(settingService.getValueByName(email_smtp_password)));
		data.put("smtpFrom", defaultString(settingService.getValueByName(email_from)));
		return XyfJsonResult.ok(data);
	}

	@PostMapping("/saveEmailConfig.do")
	public XyfJsonResult saveEmailConfig(String smtpServer, String smtpUsername, String smtpPassword, String smtpFrom) {
		if (isBlank(smtpServer) || isBlank(smtpUsername) || isBlank(smtpPassword) || isBlank(smtpFrom)) {
			return XyfJsonResult.errorMsg("SMTP配置项不能为空");
		}
		settingService.saveValueByName(email_smtp_host, smtpServer.trim());
		settingService.saveValueByName(email_smtp_username, smtpUsername.trim());
		settingService.saveValueByName(email_smtp_password, smtpPassword.trim());
		settingService.saveValueByName(email_from, smtpFrom.trim());
		return XyfJsonResult.ok();
	}

	@PostMapping("/saveDraft.do")
	public XyfJsonResult saveDraft(String to, String subject, String content, HttpServletRequest request) {
		AdminUser adminUserVo = (AdminUser) request.getSession().getAttribute("adminUser");
		MailHistory draft = new MailHistory();
		draft.setSendTime(new Date());
		draft.setFromEmail("");
		draft.setToEmail(to);
		draft.setSubject(subject);
		draft.setContent(content);
		draft.setStatus("草稿");
		draft.setFolderType("DRAFT");
		draft.setDeletedFlag(0);
		draft.setAdminUsername(adminUserVo == null ? null : adminUserVo.getUsername());
		mailHistoryService.save(draft);
		return XyfJsonResult.ok();
	}

	/**
	 * 获取邮件历史记录
	 * 
	 * @return
	 */
	@GetMapping("/getMailHistory.do")
	public XyfJsonResult getMailHistory(String folder) {
		String currentFolder = isBlank(folder) ? "SENT" : folder.toUpperCase();
		return XyfJsonResult.ok(mailHistoryService.findByFolder(currentFolder));
	}

	@PostMapping("/moveToTrash.do")
	public XyfJsonResult moveToTrash(Integer id) {
		if (id == null) {
			return XyfJsonResult.errorMsg("邮件ID不能为空");
		}
		mailHistoryService.moveToTrash(id);
		return XyfJsonResult.ok();
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String defaultString(String value) {
		return value == null ? "" : value;
	}

}

