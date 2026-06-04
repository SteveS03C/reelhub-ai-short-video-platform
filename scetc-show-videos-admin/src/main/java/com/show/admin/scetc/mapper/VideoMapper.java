package com.show.admin.scetc.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.show.admin.scetc.pojo.Video;
import com.show.admin.scetc.utils.MyMapper;

public interface VideoMapper extends MyMapper<Video> {
	@Select("select count(1) from videos")
	Long countVideos();

	@Select("select count(1) from users")
	Long countUsers();

	@Select("select ifnull(sum(like_counts),0) from videos")
	Long countVisitInteractions();

	@Select("select count(distinct user_id) from videos where create_time >= DATE_SUB(now(), interval #{days} day)")
	Long countActiveUsers(@Param("days") Integer days);
}



