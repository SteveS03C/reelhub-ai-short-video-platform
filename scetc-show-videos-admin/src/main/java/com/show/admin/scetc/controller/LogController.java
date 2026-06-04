package com.show.admin.scetc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.show.admin.scetc.utils.CommonUtils;
import com.show.admin.scetc.utils.XyfJsonResult;

/**
 * 日志
 * 
 * @author Ray
 *
 */
@RestController
@RequestMapping("/log")
public class LogController extends BasicController {

	// 返回首页
	@PostMapping("/queryAll")
	public XyfJsonResult queryAll(String page, String pageSize) {
		List<String> newList = new ArrayList<String>();
		List<String> list = redis.range(Operate_REDIS_SESSION);
		if (list == null) {
			list = new ArrayList<String>();
		}
		int p = 1;
		int s = 20;
		if (!CommonUtils.isEmpty(page)) {
			p = Integer.parseInt(page);
		}
		if (!CommonUtils.isEmpty(pageSize)) {
			s = Integer.parseInt(pageSize);
		}
		int start = (p - 1) * s;
		int end = Math.min(start + s, list.size());
		if (start < list.size()) {
			newList = list.subList(start, end);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("items", newList);
		data.put("total", list.size());
		data.put("adminCount", 1);
		return XyfJsonResult.ok(data);
	}

}



