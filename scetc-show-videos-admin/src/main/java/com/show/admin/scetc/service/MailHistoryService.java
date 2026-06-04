package com.show.admin.scetc.service;

import com.show.admin.scetc.pojo.MailHistory;
import java.util.List;

public interface MailHistoryService {
    void save(MailHistory mailHistory);
    List<MailHistory> findByFolder(String folderType);
    void moveToTrash(Integer id);
}
