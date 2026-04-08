package com.daliymove.modules.resume.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.file.FileStorageService;
import com.daliymove.modules.interview.service.InterviewPersistenceService;
import com.daliymove.modules.resume.entity.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 简历删除服务
 * - 处理简历删除的业务逻辑
 * - 删除关联的存储文件、面试会话和数据库记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeDeleteService {

    private final ResumePersistenceService persistenceService;
    private final InterviewPersistenceService interviewPersistenceService;
    private final FileStorageService storageService;

    /**
     * 删除简历
     *
     * @param id 简历ID
     * @throws BusinessException 如果简历不存在
     */
    public void deleteResume(Long id) {
        log.info("收到删除简历请求: id={}", id);

        Resume resume = persistenceService.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        try {
            storageService.deleteResume(resume.getStorageKey());
        } catch (Exception e) {
            log.warn("删除存储文件失败，继续删除数据库记录: {}", e.getMessage());
        }

        interviewPersistenceService.deleteSessionsByResumeId(id);

        persistenceService.deleteResume(id);

        log.info("简历删除完成: id={}", id);
    }
}