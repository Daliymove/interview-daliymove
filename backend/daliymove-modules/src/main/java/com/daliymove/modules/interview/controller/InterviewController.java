package com.daliymove.modules.interview.controller;

import com.daliymove.common.response.Result;
import com.daliymove.modules.interview.dto.CreateInterviewRequest;
import com.daliymove.modules.interview.dto.InterviewDetailDTO;
import com.daliymove.modules.interview.dto.InterviewReportDTO;
import com.daliymove.modules.interview.dto.InterviewSessionDTO;
import com.daliymove.modules.interview.dto.SubmitAnswerRequest;
import com.daliymove.modules.interview.dto.SubmitAnswerResponse;
import com.daliymove.modules.interview.service.InterviewHistoryService;
import com.daliymove.modules.interview.service.InterviewPersistenceService;
import com.daliymove.modules.interview.service.InterviewSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 面试控制器
 * - 提供模拟面试相关的API接口
 * - 包含会话创建、答案提交、报告生成等
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "模拟面试", description = "模拟面试会话创建、答案提交、报告生成等接口")
public class InterviewController {

    private final InterviewSessionService sessionService;
    private final InterviewHistoryService historyService;
    private final InterviewPersistenceService persistenceService;

    /**
     * 创建面试会话
     */
@PostMapping("/interview/sessions")
    @Operation(summary = "创建面试会话", description = "根据简历内容创建模拟面试会话，生成面试题目")
    public Result<InterviewSessionDTO> createSession(@RequestBody CreateInterviewRequest request) {
        log.info("创建面试会话，题目数量: {}", request.questionCount());
        InterviewSessionDTO session = sessionService.createSession(request);
        return Result.success(session);
    }

    @GetMapping("/interview/sessions/{sessionId}")
    @Operation(summary = "获取面试会话", description = "根据会话ID获取面试会话详情")
    public Result<InterviewSessionDTO> getSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        InterviewSessionDTO session = sessionService.getSession(sessionId);
        return Result.success(session);
    }

    @GetMapping("/interview/sessions/{sessionId}/question")
    @Operation(summary = "获取当前问题", description = "获取面试会话中的当前题目信息")
    public Result<Map<String, Object>> getCurrentQuestion(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        return Result.success(sessionService.getCurrentQuestionResponse(sessionId));
    }

    @PostMapping("/interview/sessions/{sessionId}/answers")
    @Operation(summary = "提交答案", description = "提交当前问题的答案，获取下一题或完成提示")
    public Result<SubmitAnswerResponse> submitAnswer(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @RequestBody Map<String, Object> body) {
        Integer questionIndex = (Integer) body.get("questionIndex");
        String answer = (String) body.get("answer");
        log.info("提交答案: 会话{}, 问题{}", sessionId, questionIndex);
        SubmitAnswerRequest request = new SubmitAnswerRequest(sessionId, questionIndex, answer);
        SubmitAnswerResponse response = sessionService.submitAnswer(request);
        return Result.success(response);
    }

    @GetMapping("/interview/sessions/{sessionId}/report")
    @Operation(summary = "生成面试报告", description = "根据面试答题情况生成综合评估报告")
    public Result<InterviewReportDTO> getReport(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("生成面试报告: {}", sessionId);
        InterviewReportDTO report = sessionService.generateReport(sessionId);
        return Result.success(report);
    }

    @GetMapping("/interview/sessions/unfinished/{resumeId}")
    @Operation(summary = "查找未完成面试", description = "根据简历ID查找未完成的面试会话")
    public Result<InterviewSessionDTO> findUnfinishedSession(
            @Parameter(description = "简历ID") @PathVariable Long resumeId) {
        return Result.success(sessionService.findUnfinishedSessionOrThrow(resumeId));
    }

    @PutMapping("/interview/sessions/{sessionId}/answers")
    @Operation(summary = "暂存答案", description = "暂存当前问题的答案，不提交评分")
    public Result<Void> saveAnswer(
            @Parameter(description = "会话ID") @PathVariable String sessionId,
            @RequestBody Map<String, Object> body) {
        Integer questionIndex = (Integer) body.get("questionIndex");
        String answer = (String) body.get("answer");
        log.info("暂存答案: 会话{}, 问题{}", sessionId, questionIndex);
        SubmitAnswerRequest request = new SubmitAnswerRequest(sessionId, questionIndex, answer);
        sessionService.saveAnswer(request);
        return Result.success(null);
    }

    @PostMapping("/interview/sessions/{sessionId}/complete")
    @Operation(summary = "提前交卷", description = "提前结束面试，未回答的题目按0分计算")
    public Result<Void> completeInterview(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("提前交卷: {}", sessionId);
        sessionService.completeInterview(sessionId);
        return Result.success(null);
    }

    @GetMapping("/interview/sessions/{sessionId}/details")
    @Operation(summary = "获取面试详情", description = "获取面试会话的完整详情，包含所有问题和答案")
    public Result<InterviewDetailDTO> getInterviewDetail(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        InterviewDetailDTO detail = historyService.getInterviewDetail(sessionId);
        return Result.success(detail);
    }

    @DeleteMapping("/interview/sessions/{sessionId}")
    @Operation(summary = "删除面试会话", description = "删除指定的面试会话及其所有数据")
    public Result<Void> deleteInterview(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("删除面试会话: {}", sessionId);
        persistenceService.deleteSessionBySessionId(sessionId);
        return Result.success(null);
    }

    @PostMapping("/interview/sessions/{sessionId}/reevaluate")
    @Operation(summary = "重新评估面试", description = "重新对面试答案进行AI评估")
    public Result<Void> reevaluateInterview(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        log.info("重新评估面试: {}", sessionId);
        sessionService.reevaluateInterview(sessionId);
        return Result.success(null);
    }
}