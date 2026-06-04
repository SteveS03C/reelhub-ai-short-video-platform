package com.show.admin.scetc.serviceImpl;

import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.show.admin.scetc.mapper.ReportMapper;
import com.show.admin.scetc.mapper.VideoMapper;
import com.show.admin.scetc.pojo.PageResult;
import com.show.admin.scetc.pojo.UsersReport;
import com.show.admin.scetc.pojo.UsersReportVo;
import com.show.admin.scetc.pojo.Video;
import com.show.admin.scetc.service.ReportService;

/**
 * 接口实现类
 * 
 * @author Ray
 *
 */
@Service
public class ReportServiceImp implements ReportService {

	@Autowired
	private ReportMapper reportMapper;
	@Autowired
	private VideoMapper videoMapper;

	@Override
	public PageResult queryAll(Integer page, Integer pageSize, String keyword) {
		PageHelper.startPage(page, pageSize);
		List<UsersReportVo> list = reportMapper.searchAll(keyword);
		PageInfo<UsersReportVo> pageList = new PageInfo<>(list);
		PageResult pageResult = new PageResult();
		pageResult.setPage(page);
		pageResult.setTotal(pageList.getPages());
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());
		return pageResult;
	}

	@Override
	public void undercarriageById(String reportId, String videoId, String status, String processAdmin, String processResult) {
		Video video = new Video();
		video.setId(videoId);
		video = videoMapper.selectOne(video);
		if (video != null) {
			video.setStatus(Integer.parseInt(status));
			videoMapper.updateByPrimaryKey(video);
		}

		UsersReport report = new UsersReport();
		report.setId(reportId);
		report.setProcessStatus("DONE");
		report.setProcessAdmin(processAdmin);
		report.setProcessResult(processResult);
		report.setProcessTime(new Date());
		reportMapper.updateByPrimaryKeySelective(report);
	}

}



