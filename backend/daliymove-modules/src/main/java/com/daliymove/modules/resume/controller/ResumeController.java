package com.daliymove.modules.resume.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.daliymove.common.response.Result;
import com.daliymove.modules.resume.dto.ResumeDetailDTO;
import com.daliymove.modules.resume.dto.ResumeListItemDTO;
import com.daliymove.modules.resume.service.ResumeDeleteService;
import com.daliymove.modules.resume.service.ResumeHistoryService;
import com.daliymove.modules.resume.service.ResumeUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 简历控制器
 * - 简历上传与分析
 * - 简历历史查询
 * - 简历详情获取
 * - 简历删除
 */
@Slf4j
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@Tag(name = "简历管理", description = "简历上传、分析、查询、删除等接口")
public class ResumeController {

    private final ResumeUploadService uploadService;
    private final ResumeDeleteService deleteService;
    private final ResumeHistoryService historyService;

    /**
     * 上传简历并获取分析结果
     *
     * @param file 简历文件（支持PDF、DOCX、DOC、TXT、MD等）
     * @return 简历分析结果，包含评分和建议
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传简历并分析", description = "上传简历文件，系统将自动解析并分析简历内容")
    public Result<Map<String, Object>> uploadAndAnalyze(@RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 上传简历: {}", userId, file.getOriginalFilename());
        
        Map<String, Object> result = uploadService.uploadAndAnalyze(file);
        boolean isDuplicate = (Boolean) result.get("duplicate");
        if (isDuplicate) {
            return Result.success("检测到相同简历，已返回历史分析结果", result);
        }
        return Result.success(result);
    }

    /**
     * 获取简历统计数据
     *
     * @return 统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取简历统计", description = "获取简历统计数据，包括总数、已分析数、待分析数")
    public Result<Map<String, Integer>> getStatistics() {
        List<ResumeListItemDTO> resumes = historyService.getAllResumes();
        
        int totalCount = resumes.size();
        int analyzedCount = (int) resumes.stream()
            .filter(r -> "COMPLETED".equals(r.analyzeStatus()))
            .count();
        int pendingCount = (int) resumes.stream()
            .filter(r -> "PENDING".equals(r.analyzeStatus()) || "PROCESSING".equals(r.analyzeStatus()))
            .count();
        
        return Result.success(Map.of(
            "totalCount", totalCount,
            "analyzedCount", analyzedCount,
            "pendingCount", pendingCount
        ));
    }

    /**
     * 获取所有简历列表
     *
     * @return 简历列表
     */
    @GetMapping("/history")
    @Operation(summary = "获取简历历史列表", description = "获取当前用户的所有简历历史记录")
    public Result<List<ResumeListItemDTO>> getAllResumes() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<ResumeListItemDTO> resumes = historyService.getAllResumes();
        return Result.success(resumes);
    }

    /**
     * 获取简历详情（包含分析历史）
     *
     * @param id 简历ID
     * @return 简历详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取简历详情", description = "根据简历ID获取简历详情，包含分析历史和面试记录")
    public Result<ResumeDetailDTO> getResumeDetail(@PathVariable Long id) {
        ResumeDetailDTO detail = historyService.getResumeDetail(id);
        return Result.success(detail);
    }

    /**
     * 导出简历分析报告为PDF
     *
     * @param id 简历ID
     * @return PDF文件
     */
    @GetMapping("/{id}/export")
    @Operation(summary = "导出简历分析报告", description = "将简历分析结果导出为PDF文件")
    public ResponseEntity<byte[]> exportAnalysisPdf(@PathVariable Long id) {
        try {
            var result = historyService.exportAnalysisPdf(id);
            String filename = URLEncoder.encode(result.filename(), StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(result.pdfBytes());
        } catch (Exception e) {
            log.error("导出PDF失败: resumeId={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除简历
     *
     * @param id 简历ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除简历", description = "删除指定ID的简历及其关联数据")
    public Result<Void> deleteResume(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 删除简历: {}", userId, id);
        
        deleteService.deleteResume(id);
        return Result.success();
    }

    /**
     * 重新分析简历（手动重试）
     * 用于分析失败后的重试
     *
     * @param id 简历ID
     * @return 结果
     */
    @PostMapping("/{id}/reanalyze")
    @Operation(summary = "重新分析简历", description = "重新分析指定简历，用于分析失败后的重试")
    public Result<Void> reanalyze(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("用户 {} 重新分析简历: {}", userId, id);
        
        uploadService.reanalyze(id);
        return Result.success();
    }

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查简历服务是否正常运行")
    public Result<Map<String, String>> health() {
        return Result.success(Map.of(
            "status", "UP",
            "service", "DailyMove - Resume Service"
        ));
    }
}