package com.show.admin.scetc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.show.admin.scetc.annotation.SysLog;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import com.show.admin.scetc.pojo.AdminUser;
import com.show.admin.scetc.pojo.Bgm;
import com.show.admin.scetc.pojo.PageResult;
import com.show.admin.scetc.service.BgmService;
import com.show.admin.scetc.utils.XyfJsonResult;

/**
 * @author Ray
 */
@RestController
@RequestMapping("/bgm")
public class BgmController extends BasicController {

    @Autowired
    private BgmService bgmService;

    /**
     * 分页查询背景音乐的列表
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @SysLog
    @RequestMapping("/selectBgmList")
    public XyfJsonResult selectBgmList(String keyword, String title,
                                       @RequestParam(value = "page", required = true, defaultValue = "1") Integer page,
                                       @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize) {
        PageResult list = bgmService.queryAll(page, pageSize, keyword, title);
        return XyfJsonResult.ok(list);
    }

    /**
     * 根据状态码来更新背景音乐
     *
     * @param id
     * @param status
     * @param author
     * @param name
     * @return
     */
    @SysLog
    @RequestMapping("/updateBgm")
    public XyfJsonResult updateBgm(Long id, String status, String author, String name) {

        if (status.equals(DELETE)) {
            bgmService.deleteBgm(id);
            return XyfJsonResult.ok();
        } else if (status.equals(UPDATE)) {
            bgmService.updateBgm(id, author, name);
            return XyfJsonResult.ok();
        }
        return XyfJsonResult.errorMsg("参数错误");

    }

    /**
     * 查询一条背景音乐的详细信息
     *
     * @param id
     * @return
     */
    @SysLog
    @PostMapping("/selectResourceById")
    public XyfJsonResult selectResourceById(Long id) {
        // 根据id查询出一个背景音乐的全部信息
        Bgm bgm = bgmService.selectOne(id);
        return XyfJsonResult.ok(bgm);

    }

    /**
     * 上传音乐代码
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SysLog
    @PostMapping("/addSubmit.do")
    public @ResponseBody
    XyfJsonResult uploadMulPic(HttpServletRequest request,
                               @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        try {
            AdminUser adminUserVo = (AdminUser) request.getSession().getAttribute("adminUser");
            if (adminUserVo == null) {
                return XyfJsonResult.errorMsg("登录已失效，请重新登录后再上传");
            }
            if (file == null || file.isEmpty() || file.getSize() <= 0) {
                return XyfJsonResult.errorMsg("未接收到可上传的mp3文件");
            }

            String fileName = HtmlUtils.htmlEscape(file.getOriginalFilename());
            if (fileName == null || fileName.trim().length() == 0) {
                return XyfJsonResult.errorMsg("文件名不能为空");
            }
            String lowerName = fileName.toLowerCase();
            if (!lowerName.endsWith(".mp3")) {
                return XyfJsonResult.errorMsg("仅支持上传mp3文件");
            }

            String fosName = UUID.randomUUID().toString() + ".mp3";
            String finalPath = bgm_filePath + fosName;
            File saveFile = new File(finalPath);
            File parentFile = saveFile.getParentFile();
            if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
                return XyfJsonResult.errorMsg("BGM目录创建失败，请检查服务器磁盘权限");
            }
            if (saveFile.exists() && !saveFile.delete()) {
                return XyfJsonResult.errorMsg("目标文件被占用，请稍后重试");
            }

            try (InputStream inputStream = file.getInputStream();
                 FileOutputStream fos = new FileOutputStream(saveFile)) {
                IOUtils.copy(inputStream, fos);
                fos.flush();
            }

            Bgm bgm = new Bgm();
            bgm.setAuthor(fileName);
            bgm.setName(fileName);
            bgm.setPath("/bgm/" + fosName);
            int affectedRows = bgmService.insert(bgm);
            if (affectedRows <= 0 || bgm.getId() == null) {
                if (saveFile.exists()) {
                    saveFile.delete();
                }
                return XyfJsonResult.errorMsg("BGM文件已上传，但数据库写入失败，请重试");
            }

            List<String> uploadedNames = new ArrayList<String>();
            uploadedNames.add(fileName);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("successCount", 1);
            data.put("uploadedNames", uploadedNames);
            data.put("insertedId", bgm.getId());
            data.put("insertedName", bgm.getName());
            data.put("insertedPath", bgm.getPath());

            if (adminUserVo != null) {
                try {
                    SimpleDateFormat formate = new SimpleDateFormat();
                    String date = formate.format(new Date());
                    redis.lpush(Operate_REDIS_SESSION,
                            date + "&nbsp;&nbsp;&nbsp;" + adminUserVo.getRealName() + ":添加了背景音乐" + fileName);
                } catch (Exception logException) {
                    logException.printStackTrace();
                }
            }
            return XyfJsonResult.ok(data);
        } catch (IOException e) {
            e.printStackTrace();
            return XyfJsonResult.errorMsg("BGM上传失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return XyfJsonResult.errorMsg("BGM上传失败: " + e.getMessage());
        }
    }

}
