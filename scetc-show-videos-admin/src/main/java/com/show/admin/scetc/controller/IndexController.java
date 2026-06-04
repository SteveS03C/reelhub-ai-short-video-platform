package com.show.admin.scetc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.show.admin.scetc.annotation.SysLog;
import com.show.admin.scetc.mapper.BgmMapper;
import com.show.admin.scetc.mapper.ReportMapper;
import com.show.admin.scetc.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.show.admin.scetc.pojo.AdminUser;
import com.show.admin.scetc.utils.XyfJsonResult;

@Controller
@RequestMapping("")
public class IndexController extends BasicController {
	@Autowired
	private VideoMapper videoMapper;

	@Autowired
	private BgmMapper bgmMapper;

	@Autowired
	private ReportMapper reportMapper;


	/**
	 * 返回主頁
	 * 
	 * @param request
	 * @return
	 */
	@SysLog
	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request) {

		// 从request中获取用户的基本信息
		ModelAndView modelAndView = new ModelAndView("thymeleaf/index");
		AdminUser adminUser = (AdminUser) request.getSession().getAttribute("adminUser");
		if (adminUser == null) {
			return new ModelAndView("thymeleaf/login");
		}
		// 将数据渲染到页面上
		modelAndView.addObject("adminUser", adminUser);
		return modelAndView;
	}
	@SysLog
	@RequestMapping("/")
	public ModelAndView show(HttpServletRequest request) {

		// 从request中获取用户的基本信息
		ModelAndView modelAndView = new ModelAndView("thymeleaf/index");
		AdminUser adminUser = (AdminUser) request.getSession().getAttribute("adminUser");
		if (adminUser == null) {
			return new ModelAndView("thymeleaf/login");

		}
		// 将数据渲染到页面上
		modelAndView.addObject("adminUser", adminUser);
		return modelAndView;
	}

	/**
	 * 主页初始化代码
	 * 
	 * @return
	 */
	@SysLog
	@RequestMapping("/init")
	public XyfJsonResult init() {
		Map<String, Object> result = new HashMap<String, Object>();
		Long videoCount = videoMapper.countVideos();
		Long userCount = videoMapper.countUsers();
		Long bgmCount = bgmMapper.countBgm();
		Long activeUserCount = videoMapper.countActiveUsers(30);
		Long visitCount = videoMapper.countVisitInteractions();
		Long pendingReports = reportMapper.countPendingReports();
		result.put("visitCount", visitCount == null ? 0 : visitCount);
		result.put("videoCount", videoCount == null ? 0 : videoCount);
		result.put("userCount", userCount == null ? 0 : userCount);
		result.put("activeUserCount", activeUserCount == null ? 0 : activeUserCount);
		result.put("bgmCount", bgmCount == null ? 0 : bgmCount);
		result.put("pendingReports", pendingReports == null ? 0 : pendingReports);
		return XyfJsonResult.ok(result);
	}

	// 500 错误页面
	@RequestMapping("/500")
	public String errorPage() {
		return "thymeleaf/500";
	}

	// 404 页面
	@RequestMapping("/404")
	public String notFoundPage() {
		return "thymeleaf/404";
	}

}



