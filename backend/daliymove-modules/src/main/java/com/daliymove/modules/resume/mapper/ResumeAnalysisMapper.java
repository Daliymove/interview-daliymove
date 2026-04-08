package com.daliymove.modules.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.resume.entity.ResumeAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 简历评测 Mapper 接口
 * - 提供简历评测数据的 CRUD 操作
 * - 提供按简历 ID 查询评测记录
 */
@Mapper
public interface ResumeAnalysisMapper extends BaseMapper<ResumeAnalysis> {

    /**
     * 根据简历 ID 查找所有评测记录（按分析时间倒序）
     * @param resumeId 简历 ID
     * @return 评测记录列表
     */
    @Select("SELECT * FROM resume_analysis WHERE resume_id = #{resumeId} ORDER BY analyzed_at DESC")
    List<ResumeAnalysis> findByResumeIdOrderByAnalyzedAtDesc(@Param("resumeId") Long resumeId);

    /**
     * 根据简历 ID 查找最新评测记录
     * @param resumeId 简历 ID
     * @return 最新的评测记录
     */
    @Select("SELECT * FROM resume_analysis WHERE resume_id = #{resumeId} ORDER BY analyzed_at DESC LIMIT 1")
    ResumeAnalysis findFirstByResumeIdOrderByAnalyzedAtDesc(@Param("resumeId") Long resumeId);
}