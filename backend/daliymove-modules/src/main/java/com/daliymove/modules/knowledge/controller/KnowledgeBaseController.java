package com.daliymove.modules.knowledge.controller;

import com.daliymove.common.response.Result;
import com.daliymove.modules.knowledge.dto.KnowledgeBaseListItemDTO;
import com.daliymove.modules.knowledge.dto.KnowledgeBaseStatsDTO;
import com.daliymove.modules.knowledge.dto.QueryRequest;
import com.daliymove.modules.knowledge.dto.QueryResponse;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import com.daliymove.modules.knowledge.service.KnowledgeBaseDeleteService;
import com.daliymove.modules.knowledge.service.KnowledgeBaseListService;
import com.daliymove.modules.knowledge.service.KnowledgeBaseQueryService;
import com.daliymove.modules.knowledge.service.KnowledgeBaseUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 知识库控制器
 * - 知识库文件上传、下载
 * - 知识库查询（RAG）
 * - 知识库管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "知识库管理", description = "知识库上传、下载、查询、管理等接口")
public class KnowledgeBaseController {

    private final KnowledgeBaseUploadService uploadService;
    private final KnowledgeBaseQueryService queryService;
    private final KnowledgeBaseListService listService;
    private final KnowledgeBaseDeleteService deleteService;

    /**
     * 获取所有知识库列表
     */
    @GetMapping("/knowledgebase/list")
    @Operation(summary = "获取知识库列表", description = "获取所有知识库列表，支持排序和状态筛选")
    public Result<List<KnowledgeBaseListItemDTO>> getAllKnowledgeBases(
            @Parameter(description = "排序字段") @RequestParam(value = "sortBy", required = false) String sortBy,
            @Parameter(description = "向量化状态") @RequestParam(value = "vectorStatus", required = false) String vectorStatus) {

        VectorStatus status = null;
        if (vectorStatus != null && !vectorStatus.isBlank()) {
            try {
                status = VectorStatus.valueOf(vectorStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Result.error("无效的向量化状态: " + vectorStatus);
            }
        }

        return Result.success(listService.listKnowledgeBases(status, sortBy));
    }

    @GetMapping("/knowledgebase/{id}")
    @Operation(summary = "获取知识库详情", description = "根据ID获取知识库详情信息")
    public Result<KnowledgeBaseListItemDTO> getKnowledgeBase(
            @Parameter(description = "知识库ID") @PathVariable Long id) {
        return listService.getKnowledgeBase(id)
                .map(Result::success)
                .orElse(Result.error("知识库不存在"));
    }

    @DeleteMapping("/knowledgebase/{id}")
    @Operation(summary = "删除知识库", description = "删除指定知识库及其向量数据")
    public Result<Void> deleteKnowledgeBase(
            @Parameter(description = "知识库ID") @PathVariable Long id) {
        deleteService.deleteKnowledgeBase(id);
        return Result.success(null);
    }

    @PostMapping("/knowledgebase/query")
    @Operation(summary = "知识库问答", description = "基于选定知识库回答问题")
    public Result<QueryResponse> queryKnowledgeBase(@Valid @RequestBody QueryRequest request) {
        return Result.success(queryService.queryKnowledgeBase(request));
    }

    @PostMapping(value = "/knowledgebase/query/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "知识库流式问答", description = "基于知识库流式回答问题")
    public Flux<String> queryKnowledgeBaseStream(@Valid @RequestBody QueryRequest request) {
        log.debug("收到知识库流式查询请求: kbIds={}, question={}",
            request.knowledgeBaseIds(), request.question());
        return queryService.answerQuestionStream(request.knowledgeBaseIds(), request.question());
    }

    @GetMapping("/knowledgebase/categories")
    @Operation(summary = "获取所有分类", description = "获取知识库的所有分类列表")
    public Result<List<String>> getAllCategories() {
        return Result.success(listService.getAllCategories());
    }

    @GetMapping("/knowledgebase/category/{category}")
    @Operation(summary = "按分类获取知识库", description = "根据分类获取知识库列表")
    public Result<List<KnowledgeBaseListItemDTO>> getByCategory(
            @Parameter(description = "分类名称") @PathVariable String category) {
        return Result.success(listService.listByCategory(category));
    }

    @GetMapping("/knowledgebase/uncategorized")
    @Operation(summary = "获取未分类知识库", description = "获取所有未分类的知识库")
    public Result<List<KnowledgeBaseListItemDTO>> getUncategorized() {
        return Result.success(listService.listByCategory(null));
    }

    @PutMapping("/knowledgebase/{id}/category")
    @Operation(summary = "更新知识库分类", description = "更新指定知识库的分类")
    public Result<Void> updateCategory(
            @Parameter(description = "知识库ID") @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        listService.updateCategory(id, body.get("category"));
        return Result.success(null);
    }

    @PostMapping(value = "/knowledgebase/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传知识库", description = "上传知识库文件并进行向量化处理")
    public Result<Map<String, Object>> uploadKnowledgeBase(
            @Parameter(description = "知识库文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "知识库名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "分类") @RequestParam(value = "category", required = false) String category) {
        return Result.success(uploadService.uploadKnowledgeBase(file, name, category));
    }

    @GetMapping("/knowledgebase/{id}/download")
    @Operation(summary = "下载知识库文件", description = "下载指定的知识库原文件")
    public ResponseEntity<byte[]> downloadKnowledgeBase(
            @Parameter(description = "知识库ID") @PathVariable Long id) {
        KnowledgeBase entity = listService.getEntityForDownload(id);
        byte[] fileContent = listService.downloadFile(id);

        String filename = entity.getOriginalFilename();
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                .header(HttpHeaders.CONTENT_TYPE,
                        entity.getContentType() != null ? entity.getContentType()
                                : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(fileContent);
    }

    @GetMapping("/knowledgebase/search")
    @Operation(summary = "搜索知识库", description = "根据关键词搜索知识库")
    public Result<List<KnowledgeBaseListItemDTO>> search(
            @Parameter(description = "搜索关键词") @RequestParam("keyword") String keyword) {
        return Result.success(listService.search(keyword));
    }

    @GetMapping("/knowledgebase/stats")
    @Operation(summary = "获取知识库统计", description = "获取知识库统计数据")
    public Result<KnowledgeBaseStatsDTO> getStatistics() {
        return Result.success(listService.getStatistics());
    }

    @PostMapping("/knowledgebase/{id}/revectorize")
    @Operation(summary = "重新向量化", description = "重新对知识库进行向量化处理，用于失败后重试")
    public Result<Void> revectorize(
            @Parameter(description = "知识库ID") @PathVariable Long id) {
        uploadService.revectorize(id);
        return Result.success(null);
    }
}