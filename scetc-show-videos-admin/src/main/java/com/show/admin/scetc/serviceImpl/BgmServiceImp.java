package com.show.admin.scetc.serviceImpl;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.show.admin.scetc.mapper.BgmMapper;
import com.show.admin.scetc.pojo.Bgm;
import com.show.admin.scetc.pojo.PageResult;
import com.show.admin.scetc.service.BgmService;

/**
 * 接口实现类
 * 
 * @author Ray
 *
 */
@Service
public class BgmServiceImp implements BgmService {

	@Value("${bgm.upload.path}")
	public String bgmFilePath;

	@Autowired
	private BgmMapper bgmMapper;

	@Override
	public List<Bgm> queryAll() {
		List<Bgm> list = bgmMapper.selectAll();
		return list;
	}

	/**
	 * 分页查询bgm列表
	 */
	@Override
	public PageResult queryAll(Integer page, Integer pageSize, String keyword, String title) {

		PageHelper.startPage(page, pageSize);
		List<Bgm> list = bgmMapper.queryAll(keyword, title);
		// 3、获取分页查询后的数据
		PageInfo<Bgm> pageInfo = new PageInfo<>(list);
		// 4、封装需要返回的分页实体
		PageResult result = new PageResult();
		// 设置获取到的总记录数total：
		result.setTotal(pageInfo.getPages());
		// 设置数据集合rows：
		result.setRows(list);
		result.setRecords(pageInfo.getTotal());
		result.setPage(page);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED) // 事务
	@Override
	public void deleteBgm(Long id) {
		Bgm bgm = new Bgm();
		bgm.setId(id);
		bgm = bgmMapper.selectOne(bgm);
		if (bgm == null) {
			return;
		}
		String path = bgm.getPath();
		String fileName = path == null ? "" : path.replace("/bgm/", "");
		File file = new File(bgmFilePath, fileName);
		if (file.exists()) {
			file.delete();
		}
		bgmMapper.delete(bgm);
	}

	@Transactional(propagation = Propagation.REQUIRED) // 事务
	@Override
	public int insert(Bgm bgm) {
		// 保留每次上传记录，避免前端看到“上传成功”但数据库无新增
		return bgmMapper.insertSelective(bgm);
	}

	@Override
	public Bgm selectOne(Long id) {
		Bgm bgm = new Bgm();
		bgm.setId(id);
		bgm = bgmMapper.selectOne(bgm);
		return bgm;
	}

	@Transactional(propagation = Propagation.REQUIRED) // 事务
	@Override
	public void updateBgm(Long id, String author, String name) {
		Bgm bgm = new Bgm();
		bgm.setId(id);
		Bgm example = bgmMapper.selectOne(bgm);
		example.setName(name);
		example.setAuthor(author);
		bgmMapper.updateByPrimaryKeySelective(example);

	}

}

