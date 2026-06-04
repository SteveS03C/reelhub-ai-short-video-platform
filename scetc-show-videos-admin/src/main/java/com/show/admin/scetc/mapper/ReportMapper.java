package com.show.admin.scetc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.show.admin.scetc.pojo.UsersReport;
import com.show.admin.scetc.pojo.UsersReportVo;
import com.show.admin.scetc.utils.MyMapper;

public interface ReportMapper extends MyMapper<UsersReport> {
	/**
	 * 查询全部的举报信息
	 * 
	 * @return
	 */
	List<UsersReportVo> searchAll(@Param("keyword") String keyword);

	@Select("select count(1) from users_report where process_status is null or process_status = 'PENDING'")
	Long countPendingReports();

}



