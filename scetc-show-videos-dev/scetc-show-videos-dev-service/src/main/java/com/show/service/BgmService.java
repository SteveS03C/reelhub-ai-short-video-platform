package com.show.service;

import java.util.List;

import com.show.pojo.Bgm;
/**
 * 
 * @author 2016wlw2 信息苏
 * 创建时间：2018年7月4日 下午8:57:53
 */
public interface BgmService 
{
  //查询bgm列表
	List<Bgm> queryBgmList();
  //查询单个bgmid
    public Bgm queryBgmById(Long bgmId);

  // 新增用户上传的bgm
    public void insertBgm(Bgm bgm);


}



