package com.show.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.show.utils.JsonUtils;
import com.show.utils.XyfJsonResult;

@RestController
@RequestMapping("/ai/video")
public class AiVideoQaController extends BasicController {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final DecimalFormat SECOND_FORMAT = new DecimalFormat("0.00");

	@Value("${qwen.api.key:}")
	private String qwenApiKey;

	@Value("${qwen.api.endpoint:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
	private String qwenApiEndpoint;

	@Value("${qwen.api.model:qvq-max-2025-03-25}")
	private String qwenApiModel;

	@Value("${qwen.video.frame-count:4}")
	private int qwenFrameCount;

	@PostMapping(value = "/qaUpload", headers = "content-type=multipart/form-data")
	public XyfJsonResult qaUpload(String question, String videoDesc, String videoSeconds,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return XyfJsonResult.errorMsg("没有接收到可分析的视频文件");
		}

		String fileName = StringUtils.defaultIfBlank(file.getOriginalFilename(), UUID.randomUUID().toString() + ".mp4");
		Path workDir = null;
		try {
			workDir = Files.createTempDirectory("show-video-ai-");
			Path savedVideo = workDir.resolve(fileName);
			Files.copy(file.getInputStream(), savedVideo, StandardCopyOption.REPLACE_EXISTING);
			String answer = analyzeVideo(savedVideo.toFile(), parseVideoSeconds(videoSeconds), question, videoDesc, workDir);
			return XyfJsonResult.ok(buildAnswerData(answer));
		} catch (Exception e) {
			e.printStackTrace();
			return XyfJsonResult.errorMsg("AI问答失败: " + e.getMessage());
		} finally {
			deleteQuietly(workDir);
		}
	}

	@PostMapping("/qaByPath")
	public XyfJsonResult qaByPath(String question, String videoDesc, String videoPath, String videoSeconds) {
		if (StringUtils.isBlank(videoPath)) {
			return XyfJsonResult.errorMsg("视频路径不能为空");
		}

		Path workDir = null;
		try {
			File videoFile = resolveVideoFile(videoPath);
			if (!videoFile.exists() || !videoFile.isFile()) {
				return XyfJsonResult.errorMsg("未找到可分析的视频文件");
			}

			workDir = Files.createTempDirectory("show-video-ai-");
			String answer = analyzeVideo(videoFile, parseVideoSeconds(videoSeconds), question, videoDesc, workDir);
			return XyfJsonResult.ok(buildAnswerData(answer));
		} catch (Exception e) {
			e.printStackTrace();
			return XyfJsonResult.errorMsg("AI问答失败: " + e.getMessage());
		} finally {
			deleteQuietly(workDir);
		}
	}

	private Map<String, Object> buildAnswerData(String answer) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("answer", answer);
		data.put("model", qwenApiModel);
		data.put("frameCount", qwenFrameCount);
		return data;
	}

	private File resolveVideoFile(String videoPath) {
		String normalizedPath = videoPath.replace("\\", "/");
		if (normalizedPath.startsWith("http://") || normalizedPath.startsWith("https://")) {
			throw new IllegalArgumentException("当前仅支持本地或服务端已落盘的视频分析");
		}
		if (normalizedPath.startsWith("/")) {
			normalizedPath = normalizedPath.substring(1);
		}
		return new File(FILe_SPACE, normalizedPath);
	}

	private String analyzeVideo(File videoFile, double videoSeconds, String question, String videoDesc, Path workDir)
			throws Exception {
		if (StringUtils.isBlank(qwenApiKey)) {
			throw new IllegalStateException("未配置通义千问 API Key");
		}

		List<File> frames = extractFrames(videoFile, videoSeconds, workDir);
		if (frames.isEmpty()) {
			throw new IllegalStateException("未能从视频中提取有效画面");
		}

		String answer = callQwen(frames, buildPrompt(question, videoDesc));
		if (StringUtils.isBlank(answer)) {
			throw new IllegalStateException("模型未返回有效回答");
		}
		return answer.trim();
	}

	private String buildPrompt(String question, String videoDesc) {
		String safeQuestion = StringUtils.defaultIfBlank(question, "总结一下这个视频讲了什么");
		String safeDesc = StringUtils.defaultIfBlank(videoDesc, "无");
		return "你是一个多模态 AI 助手。请优先结合给定关键帧与补充描述回答用户问题；"
				+ "如果问题超出画面信息，也可以直接给出通用回答。"
				+ "当画面不足以支撑某个细节判断时，请明确说明“不足以判断”，不要编造。"
				+ "\n补充描述：" + safeDesc
				+ "\n用户问题：" + safeQuestion;
	}

	private List<File> extractFrames(File videoFile, double videoSeconds, Path workDir) throws Exception {
		File ffmpegFile = resolveFfmpegFile();
		if (!ffmpegFile.exists()) {
			throw new IllegalStateException("ffmpeg 未配置或路径不可用");
		}

		int targetFrameCount = qwenFrameCount <= 0 ? 4 : qwenFrameCount;
		List<Double> timestamps = buildTimestamps(videoSeconds, targetFrameCount);
		List<File> frames = new ArrayList<File>();
		for (int i = 0; i < timestamps.size(); i++) {
			File outputFile = workDir.resolve("frame_" + i + ".jpg").toFile();
			runFfmpeg(ffmpegFile, videoFile, outputFile, timestamps.get(i));
			if (outputFile.exists() && outputFile.length() > 0) {
				frames.add(outputFile);
			}
		}

		if (frames.isEmpty()) {
			File outputFile = workDir.resolve("frame_fallback.jpg").toFile();
			runFfmpeg(ffmpegFile, videoFile, outputFile, 0.5D);
			if (outputFile.exists() && outputFile.length() > 0) {
				frames.add(outputFile);
			}
		}
		return frames;
	}

	private List<Double> buildTimestamps(double videoSeconds, int frameCount) {
		List<Double> timestamps = new ArrayList<Double>();
		if (videoSeconds <= 0D) {
			for (int i = 0; i < frameCount; i++) {
				timestamps.add(0.5D + i);
			}
			return timestamps;
		}

		double safeDuration = Math.max(videoSeconds, 1D);
		for (int i = 1; i <= frameCount; i++) {
			double timestamp = (safeDuration * i) / (frameCount + 1);
			timestamps.add(Math.max(0D, timestamp));
		}
		return timestamps;
	}

	private void runFfmpeg(File ffmpegFile, File videoFile, File outputFile, double second) throws Exception {
		List<String> command = Arrays.asList(
				ffmpegFile.getAbsolutePath(),
				"-ss",
				SECOND_FORMAT.format(second),
				"-y",
				"-i",
				videoFile.getAbsolutePath(),
				"-frames:v",
				"1",
				"-q:v",
				"2",
				outputFile.getAbsolutePath());

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		String output = readProcessOutput(process.getInputStream());
		int exitCode = process.waitFor();
		if (exitCode != 0 || !outputFile.exists()) {
			throw new IllegalStateException("ffmpeg 抽帧失败: " + output);
		}
	}

	private File resolveFfmpegFile() {
		List<String> candidates = new ArrayList<String>();
		if (StringUtils.isNotBlank(FFMPEGEXE)) {
			candidates.add(FFMPEGEXE);
		}
		candidates.add("D:/ffmpeg/ffmepg/bin/ffmpeg.exe");
		candidates.add("D:/ffmpeg/bin/ffmpeg.exe");
		candidates.add("C:/ffmpeg/bin/ffmpeg.exe");
		candidates.add("C:/Program Files (x86)/Lenovo/LegionZone/2.0.23.3251/SEGamingAI/services/editor/ffmpeg.exe");

		for (String candidate : candidates) {
			if (StringUtils.isBlank(candidate)) {
				continue;
			}
			File file = new File(candidate);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}
		return new File(StringUtils.defaultString(FFMPEGEXE, ""));
	}

	private String callQwen(List<File> frames, String prompt) throws Exception {
		List<Map<String, Object>> contentBlocks = new ArrayList<Map<String, Object>>();
		for (File frame : frames) {
			byte[] bytes = Files.readAllBytes(frame.toPath());
			Map<String, Object> imageUrl = new LinkedHashMap<String, Object>();
			imageUrl.put("url", "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(bytes));

			Map<String, Object> imageBlock = new LinkedHashMap<String, Object>();
			imageBlock.put("type", "image_url");
			imageBlock.put("image_url", imageUrl);
			contentBlocks.add(imageBlock);
		}

		Map<String, Object> textBlock = new LinkedHashMap<String, Object>();
		textBlock.put("type", "text");
		textBlock.put("text", prompt);
		contentBlocks.add(textBlock);

		Map<String, Object> userMessage = new LinkedHashMap<String, Object>();
		userMessage.put("role", "user");
		userMessage.put("content", contentBlocks);

		Map<String, Object> payload = new LinkedHashMap<String, Object>();
		payload.put("model", qwenApiModel);
		payload.put("stream", true);
		payload.put("stream_options", Collections.singletonMap("include_usage", true));
		payload.put("messages", Collections.singletonList(userMessage));

		String responseBody = doPostJson(qwenApiEndpoint, JsonUtils.objectToJson(payload), true);
		String answer = extractStreamContent(responseBody);
		if (StringUtils.isBlank(answer)) {
			throw new IllegalStateException("通义千问未返回可解析的回答");
		}
		return answer;
	}

	private String extractStreamContent(String responseBody) throws Exception {
		if (StringUtils.isBlank(responseBody)) {
			return "";
		}
		String[] lines = responseBody.split("\\r?\\n");
		StringBuilder contentBuilder = new StringBuilder();
		StringBuilder errorBuilder = new StringBuilder();
		for (String line : lines) {
			String trimmed = StringUtils.trimToEmpty(line);
			if (!trimmed.startsWith("data:")) {
				continue;
			}
			String data = StringUtils.trimToEmpty(trimmed.substring(5));
			if (StringUtils.isBlank(data) || "[DONE]".equals(data)) {
				continue;
			}
			JsonNode chunk = MAPPER.readTree(data);
			if (chunk.has("error")) {
				String errorMessage = chunk.path("error").path("message").asText();
				if (StringUtils.isNotBlank(errorMessage)) {
					errorBuilder.append(errorMessage);
				}
				continue;
			}
			JsonNode choices = chunk.path("choices");
			if (!choices.isArray() || choices.size() == 0) {
				continue;
			}
			JsonNode delta = choices.path(0).path("delta");
			String deltaContent = extractContent(delta.path("content"));
			if (StringUtils.isNotBlank(deltaContent)) {
				contentBuilder.append(deltaContent);
			}
		}
		if (contentBuilder.length() > 0) {
			return contentBuilder.toString();
		}
		if (errorBuilder.length() > 0) {
			throw new IllegalStateException(errorBuilder.toString());
		}
		return "";
	}

	private String extractContent(JsonNode contentNode) {
		if (contentNode == null || contentNode.isMissingNode() || contentNode.isNull()) {
			return "";
		}
		if (contentNode.isTextual()) {
			return contentNode.asText();
		}
		if (contentNode.isArray()) {
			StringBuilder builder = new StringBuilder();
			for (JsonNode item : contentNode) {
				if (item.isTextual()) {
					builder.append(item.asText());
					continue;
				}
				if (item.has("text")) {
					builder.append(item.path("text").asText());
				}
			}
			return builder.toString();
		}
		return contentNode.toString();
	}

	private String doPostJson(String endpoint, String body, boolean streamResponse) throws Exception {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(endpoint).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(120000);
			connection.setRequestProperty("Authorization", "Bearer " + qwenApiKey);
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Accept", streamResponse ? "text/event-stream" : "application/json");

			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(body.getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
			}

			int statusCode = connection.getResponseCode();
			String responseBody = readHttpBody(statusCode >= 200 && statusCode < 300
					? connection.getInputStream()
					: connection.getErrorStream());
			if (statusCode < 200 || statusCode >= 300) {
				throw new IllegalStateException("通义千问接口异常: HTTP " + statusCode + " " + responseBody);
			}
			return responseBody;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private String readHttpBody(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return "";
		}
		try (InputStream is = inputStream; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[4096];
			int length;
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			return new String(baos.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	private String readProcessOutput(InputStream inputStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}
		}
		return builder.toString();
	}

	private double parseVideoSeconds(String videoSeconds) {
		if (StringUtils.isBlank(videoSeconds)) {
			return 0D;
		}
		try {
			return Double.parseDouble(videoSeconds);
		} catch (Exception e) {
			return 0D;
		}
	}

	private void deleteQuietly(Path path) {
		if (path == null || !Files.exists(path)) {
			return;
		}
		File file = path.toFile();
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteQuietly(child.toPath());
				}
			}
		}
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			// 临时文件清理失败不影响主流程
		}
	}
}
