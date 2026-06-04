package com.show.admin.scetc.controller;

import com.show.admin.scetc.annotation.SysLog;
import com.show.admin.scetc.pojo.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.show.admin.scetc.pojo.PageResult;
import com.show.admin.scetc.service.ReportService;
import com.show.admin.scetc.utils.XyfJsonResult;
import javax.servlet.http.HttpServletRequest;

/**
 * 举报模块 select u.username,ur.*,v.video_path,u2.username as publisher from
 * users_report ur left join users u on u.id = ur.userid left join videos v on
 * v.id = ur.deal_video_id left join users u2 on u2.id=ur.deal_user_id
 * 
 * @author Ray
 */
@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@PostMapping("/queryAll")
	@SysLog
	public XyfJsonResult queryAll(String keyword, Integer page, Integer pageSize) {
		PageResult list = reportService.queryAll(page, pageSize, keyword);
		return XyfJsonResult.ok(list);
	}

	@PostMapping("/undercarriage")
	@SysLog
	public XyfJsonResult undercarriage(String reportId, String id, String status, HttpServletRequest request) {
		AdminUser adminUser = (AdminUser) request.getSession().getAttribute("adminUser");
		String adminName = adminUser == null ? "系统" : adminUser.getUsername();
		String result = "2".equals(status) ? "已下架视频" : "已恢复上架";
		reportService.undercarriageById(reportId, id, status, adminName, result);
		return XyfJsonResult.ok();
	}
}



