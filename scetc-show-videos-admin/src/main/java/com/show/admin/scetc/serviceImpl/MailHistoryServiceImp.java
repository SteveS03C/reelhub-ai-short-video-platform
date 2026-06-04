package com.show.admin.scetc.serviceImpl;

import com.show.admin.scetc.mapper.MailHistoryMapper;
import com.show.admin.scetc.pojo.MailHistory;
import com.show.admin.scetc.service.MailHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import tk.mybatis.mapper.entity.Example;

@Service
public class MailHistoryServiceImp implements MailHistoryService {

    @Autowired
    private MailHistoryMapper mailHistoryMapper;

    @Override
    public void save(MailHistory mailHistory) {
        mailHistoryMapper.insert(mailHistory);
    }

    @Override
    public List<MailHistory> findByFolder(String folderType) {
        Example example = new Example(MailHistory.class);
        Example.Criteria criteria = example.createCriteria();
        if ("TRASH".equalsIgnoreCase(folderType)) {
            criteria.andEqualTo("deletedFlag", 1);
        } else {
            criteria.andEqualTo("deletedFlag", 0);
            criteria.andEqualTo("folderType", folderType);
        }
        example.orderBy("id").desc();
        return mailHistoryMapper.selectByExample(example);
    }

    @Override
    public void moveToTrash(Integer id) {
        MailHistory mailHistory = new MailHistory();
        mailHistory.setId(id);
        mailHistory.setDeletedFlag(1);
        mailHistoryMapper.updateByPrimaryKeySelective(mailHistory);
    }
}
