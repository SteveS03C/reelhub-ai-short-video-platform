package com.show.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.show.pojo.Bgm;
import com.show.service.BgmService;
import com.show.utils.XyfJsonResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/bgm")
@Api(value = "背景音乐", tags = { "背景音乐业务controller" })
public class BgmController extends BasicController {
	@Autowired
	private BgmService bgmService;

	@ApiOperation(value = "列表", notes = "获取背景音乐列表")
	@PostMapping("/list")
	public XyfJsonResult list() {
		
		return XyfJsonResult.ok(bgmService.queryBgmList());
	}

	@ApiOperation(value = "列表", notes = "获取视频分类列表")
	@PostMapping("/listVideoCategory")
	public XyfJsonResult listVideoCategory() {
		
		return XyfJsonResult.ok(bgmService.queryBgmList());
	}

	@ApiOperation(value = "上传背景音乐", notes = "小程序上传自定义背景音乐")
	@PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
	public XyfJsonResult upload(String userId, MultipartFile file) {
		if (StringUtils.isBlank(userId)) {
			return XyfJsonResult.errorMsg("用户信息不能为空");
		}
		if (file == null || file.isEmpty()) {
			return XyfJsonResult.errorMsg("请选择要上传的mp3文件");
		}

		String fileName = file.getOriginalFilename();
		if (StringUtils.isBlank(fileName) || !StringUtils.endsWithIgnoreCase(fileName, ".mp3")) {
			return XyfJsonResult.errorMsg("仅支持上传mp3文件");
		}

		String saveName = UUID.randomUUID().toString() + ".mp3";
		File targetDir = new File(FILe_SPACE, "bgm");
		File targetFile = new File(targetDir, saveName);
		try {
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
			FileUtils.copyInputStreamToFile(file.getInputStream(), targetFile);

			Bgm bgm = new Bgm();
			bgm.setAuthor("用户" + userId);
			bgm.setName(fileName);
			bgm.setPath("/bgm/" + saveName);
			bgmService.insertBgm(bgm);
			return XyfJsonResult.ok(bgm);
		} catch (IOException e) {
			e.printStackTrace();
			return XyfJsonResult.errorMsg("上传背景音乐失败");
		}
	}
	
}



