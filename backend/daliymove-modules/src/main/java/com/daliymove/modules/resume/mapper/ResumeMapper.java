package com.daliymove.modules.resume.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.resume.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

/**
 * 简历 Mapper 接口
 * - 提供简历数据的 CRUD 操作
 * - 提供文件哈希查询（用于去重）
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

    /**
     * 根据文件哈希查找简历
     * @param fileHash 文件哈希值
     * @return 简历实体（Optional 包装）
     */
    @Select("SELECT * FROM resume WHERE file_hash = #{fileHash} LIMIT 1")
    Optional<Resume> findByFileHash(@Param("fileHash") String fileHash);

    /**
     * 检查文件哈希是否存在
     * @param fileHash 文件哈希值
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) FROM resume WHERE file_hash = #{fileHash} LIMIT 1")
    boolean existsByFileHash(@Param("fileHash") String fileHash);
}