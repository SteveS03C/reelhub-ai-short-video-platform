package com.show.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.show.pojo.Bgm;
import com.show.pojo.PageResult;
import com.show.pojo.VideoStatusEnum;
import com.show.pojo.Videos;
import com.show.pojo.VideosVo;
import com.show.service.BgmService;
import com.show.service.VideoService;
import com.show.utils.FetchVideo;
import com.show.utils.MergeVideo;
import com.show.utils.XyfJsonResult;
import com.show.vo.CommentsVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @author 2016wlw2 信息苏 创建时间：2018年6月28日 下午12:07:38
 */
@RestController
@Api(value = "视频相关的业务数据接口", tags = { "视频上传" })
@RequestMapping("/video")
public class VideoController extends BasicController {

	@Autowired
	private BgmService bgmService;
	@Autowired
	private VideoService videoService;

	/**
	 * @param userId
	 * @param bgmId
	 * @param duration
	 * @param tmpWidth
	 * @param tmpHeight
	 * @param desc
	 * @param videoCategory
	 * @param videoFilter
	 * @param file
	 * @return
	 */
	@ApiOperation(value = "用户上传视频", notes = "用户上传视频接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"), //// 可以不指定
			@ApiImplicitParam(name = "videoSeconds", value = "视频时间", required = true, dataType = "double", paramType = "form"), // 参数类型为form
			@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "int", paramType = "form"),
			@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "int", paramType = "form"),
			@ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "query"), // 可以不指定
			@ApiImplicitParam(name = "videoCategory", value = "视频分类", required = false, dataType = "String", paramType = "query")// 可以不指定

	})
	@PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
	public XyfJsonResult uploadFace(String userId, String bgmId, double videoSeconds, int videoWidth, int videoHeight,
			String desc, String videoCategory, String videoFilter,
			@ApiParam(value = "短视频", required = true) MultipartFile file) {

		if (StringUtils.isBlank(userId)) {
			return XyfJsonResult.errorException("用户id不能为空...");
		}
		if (file == null || file.isEmpty()) {
			return XyfJsonResult.errorException("上传视频不能为空...");
		}
		if (StringUtils.isBlank(videoFilter)) {
			videoFilter = Filter_DEFINE;
		}
		// 文件保存命名空间

		// 保存到数据库路径 相对路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPath = "/" + userId + "/video";

		String finalVideoPath = "";
		String fileName = null;
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		fileName = file.getOriginalFilename();
		try {
			if (StringUtils.isBlank(fileName)) {
				return XyfJsonResult.errorException("上传失败了");
			}
			// 文件上传的最终保存路径
			finalVideoPath = FILe_SPACE + "/" + uploadPathDB + "/" + fileName;
			// 数据库保存路径
			uploadPathDB += ("/" + fileName);
			File outFile = new File(finalVideoPath);
			if (outFile.getParentFile() != null && !outFile.getParentFile().isDirectory()) {
				outFile.getParentFile().mkdirs();
			}

			fileOutputStream = new FileOutputStream(outFile);
			inputStream = file.getInputStream();

			IOUtils.copy(inputStream, fileOutputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return XyfJsonResult.errorException("上传失败了");
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			IOUtils.closeQuietly(fileOutputStream);
			IOUtils.closeQuietly(inputStream);
		}

		// 检查ffmpeg是否存在
		File ffmpegFile = new File(FFMPEGEXE);
		if (ffmpegFile.exists()) {
			try {
				MergeVideo tool = new MergeVideo(FFMPEGEXE);
				if (StringUtils.isNotBlank(bgmId)) {
					Bgm bgm = bgmService.queryBgmById(Long.valueOf(bgmId));
					if (bgm != null && StringUtils.isNotBlank(bgm.getPath())) {
						String mp3InputPath = FILe_SPACE + bgm.getPath();
						String outPathName = UUID.randomUUID().toString() + ".mp4";
						uploadPathDB = "/" + userId + "/video" + "/" + outPathName;
						String outputStream = FILe_SPACE + "/" + uploadPathDB;
						tool.convertor(mp3InputPath, finalVideoPath, videoSeconds, outputStream, videoFilter);
						finalVideoPath = outputStream;
					}
				} else if (!videoFilter.equals(Filter_DEFINE)) {
					String outPathName = UUID.randomUUID().toString() + ".mp4";
					uploadPathDB = "/" + userId + "/video" + "/" + outPathName;
					String outputStream = FILe_SPACE + "/" + uploadPathDB;
					tool.convertor(null, finalVideoPath, videoSeconds, outputStream, videoFilter);
					finalVideoPath = outputStream;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (ffmpegFile.exists()) {
			try {
				FetchVideo videoInfo = new FetchVideo(FFMPEGEXE);
				String safeFileName = StringUtils.defaultIfBlank(fileName, UUID.randomUUID().toString() + ".mp4");
				String fileNamePrefix = safeFileName.contains(".") ? safeFileName.split("\\.")[0] : safeFileName;
				coverPath = coverPath + "/" + fileNamePrefix + UUID.randomUUID().toString() + ".jpg"; // 相对路径放到数据库里
				videoInfo.getcover(finalVideoPath, FILe_SPACE + coverPath);// 输出转换后的图片
			} catch (Exception e) {
				e.printStackTrace();
				coverPath = "/default/cover.jpg";
			}
		} else {
			// ffmpeg不存在，使用默认封面路径
			coverPath = "/default/cover.jpg";
		}
		// 保存视频到数据库
		Videos video = new Videos();
		video.setVideoCategory(videoCategory);
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float) videoSeconds);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setCreateTime(new Date());
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPath);
		video.setLikeCounts(0L);
		video.setVideoFileter(videoFilter);
		video.setStatus(VideoStatusEnum.Success.value);
		String videoId = videoService.saveVideo(video);
		return XyfJsonResult.ok(videoId);// 返回200

	}
	/**
	 * 用于上传封面，目前已经整合到·上传视频的接口中
	 */
//	@ApiOperation(value = "上传封面", notes = "上传封面的接口")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
//			@ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form") })
//	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
//	public XyfJsonResult uploadCover(String userId, String videoId,
//			@ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {
//
//		if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
//			return XyfJsonResult.errorMsg("视频主键id和用户id不能为空...");
//		}
//
//		// 文件保存的命名空间
//		// 保存到数据库中的相对路径
//		String uploadPathDB = "/" + userId + "/video";
//
//		FileOutputStream fileOutputStream = null;
//		InputStream inputStream = null;
//		// 文件上传的最终保存路径
//		String finalCoverPath = "";
//		try {
//			if (file != null) {
//
//				String fileName = file.getOriginalFilename();
//				if (StringUtils.isNotBlank(fileName)) {
//
//					finalCoverPath = FILe_SPACE + uploadPathDB + "/" + fileName;
//					// 设置数据库保存的路径
//					uploadPathDB += ("/" + fileName);
//
//					File outFile = new File(finalCoverPath);
//					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
//						// 创建父文件夹
//						outFile.getParentFile().mkdirs();
//					}
//
//					fileOutputStream = new FileOutputStream(outFile);
//					inputStream = file.getInputStream();
//					IOUtils.copy(inputStream, fileOutputStream);
//				}
//
//			} else {
//				return XyfJsonResult.errorMsg("上传出错...");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return XyfJsonResult.errorMsg("上传出错...");
//		} finally {
//			if (fileOutputStream != null) {
//				fileOutputStream.flush();
//				fileOutputStream.close();
//			}
//		}
//
//		videoService.updateVideo(videoId, uploadPathDB);
//
//		return XyfJsonResult.ok();
//	}

	/**
	 * 分页和搜索查询列表 issave=1 需要保存 issave=0 不需要保存 或者为空的时候
	 * 
	 * @param video
	 * @param isSaveRecord
	 * @param page
	 * @return
	 */
	// 热搜
	@PostMapping(value = "/hot") // isSaveRecord 是否保存记录
	public XyfJsonResult hot() {
		return XyfJsonResult.ok(videoService.getHotWords());
	}

	@PostMapping(value = "/showAll") // isSaveRecord 是否保存记录
	public XyfJsonResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page, String category) {
		if (page == null) {
			page = 1;
		}
		PageResult pageResult = videoService.getAllVideos(video, isSaveRecord, page, PAGE_SIZE, category);
		System.out.println(pageResult.toString());
		return XyfJsonResult.ok(pageResult);
	}

	@PostMapping(value = "/userLike") // isSaveRecord 是否保存记录
	public XyfJsonResult userLike(String userId, String videoId, String videoCreaterId) {
		videoService.userLikeVideo(userId, videoId, videoCreaterId);
		return XyfJsonResult.ok();
	}

	@PostMapping(value = "/userUnLike") // isSaveRecord 是否保存记录
	public XyfJsonResult userUnLike(String userId, String videoId, String videoCreaterId) {
		videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
		return XyfJsonResult.ok();
	}

	// 保存用户的评论到数据库
	@PostMapping(value = "/saveComments") // isSaveRecord 是否保存记录
	public XyfJsonResult saveComments(String userId, String videoId, String comment) {
		videoService.saveComment(userId, videoId, comment);
		return XyfJsonResult.ok();
	}

	// 保存用户的评论到数据库
	@PostMapping(value = "/queryCommentsByVideoId")
	public XyfJsonResult queryCommentsByVideoId(String videoId) {
		List<CommentsVo> commentsAll = videoService.queryCommentsByVideoId(videoId);
		return XyfJsonResult.ok(commentsAll);// 返回当前视频的所有评论
	}

	// 查询当前用户的所有的视频
	@PostMapping(value = "/queryVideosByUser")
	public XyfJsonResult queryVideosByUser(String userId) {
		List<VideosVo> listVideos = videoService.queryVideosByUser(userId);
		return XyfJsonResult.ok(listVideos);
	}

	/**
	 * @param userId      举报人的id
	 * @param dealUserId  被举报用户的id
	 * @param dealVideoId 被举报视频的id
	 * @return
	 */
	// 举报视频
	@PostMapping("/report")
	public XyfJsonResult reportVideosByUser(String userId, String dealUserId, String dealVideoId, String title,
			String content) {
		videoService.reportVideoByUser(dealUserId, dealVideoId, userId, title, content);
		return XyfJsonResult.ok();
	}

}


