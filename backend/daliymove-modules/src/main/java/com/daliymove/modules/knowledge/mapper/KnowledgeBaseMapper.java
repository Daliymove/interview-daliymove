package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 知识库Mapper接口
 * - 提供知识库的数据访问操作
 * - 支持文件哈希去重查询
 * - 支持向量化状态查询
 */
@Mapper
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBase> {

    /**
     * 根据文件哈希值查找知识库（用于去重）
     * @param fileHash 文件SHA-256哈希值
     * @return 知识库对象
     */
    @Select("SELECT * FROM knowledge_base WHERE file_hash = #{fileHash}")
    KnowledgeBase findByFileHash(@Param("fileHash") String fileHash);

    /**
     * 根据向量化状态查找知识库列表，按上传时间降序
     * @param vectorStatus 向量化状态
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge_base WHERE vector_status = #{vectorStatus} ORDER BY uploaded_at DESC")
    List<KnowledgeBase> findByVectorStatusOrderByUploadedAtDesc(@Param("vectorStatus") String vectorStatus);

    /**
     * 根据分类查找知识库列表，按上传时间降序
     * @param category 分类名称
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge_base WHERE category = #{category} ORDER BY uploaded_at DESC")
    List<KnowledgeBase> findByCategoryOrderByUploadedAtDesc(@Param("category") String category);

    /**
     * 查找无分类的知识库列表，按上传时间降序
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge_base WHERE category IS NULL ORDER BY uploaded_at DESC")
    List<KnowledgeBase> findByCategoryIsNullOrderByUploadedAtDesc();

    /**
     * 查找所有知识库，按上传时间降序
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge_base ORDER BY uploaded_at DESC")
    List<KnowledgeBase> findAllByOrderByUploadedAtDesc();

    /**
     * 根据向量化状态统计数量
     * @param vectorStatus 向量化状态
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM knowledge_base WHERE vector_status = #{vectorStatus}")
    int countByVectorStatus(@Param("vectorStatus") String vectorStatus);

    /**
     * 统计所有知识库的访问次数总和
     * @return 访问次数总和
     */
    @Select("SELECT COALESCE(SUM(access_count), 0) FROM knowledge_base")
    long sumAccessCount();

    /**
     * 获取所有分类（去重）
     * @return 分类列表
     */
    @Select("SELECT DISTINCT category FROM knowledge_base WHERE category IS NOT NULL ORDER BY category")
    List<String> findAllCategories();

    /**
     * 搜索知识库（按名称或文件名）
     * @param keyword 关键词
     * @return 知识库列表
     */
    @Select("SELECT * FROM knowledge_base WHERE name LIKE CONCAT('%', #{keyword}, '%') OR original_filename LIKE CONCAT('%', #{keyword}, '%') ORDER BY uploaded_at DESC")
    List<KnowledgeBase> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 批量增加问题计数
     * @param ids 知识库ID列表
     * @return 更新的行数
     */
    @Update("<script>" +
            "UPDATE knowledge_base SET question_count = COALESCE(question_count, 0) + 1, updated_at = NOW() WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int incrementQuestionCountBatch(@Param("ids") List<Long> ids);
}