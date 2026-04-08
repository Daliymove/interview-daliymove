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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 面试控制器
 * - 提供模拟面试相关的API接口
 * - 包含会话创建、答案提交、报告生成等
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewSessionService sessionService;
    private final InterviewHistoryService historyService;
    private final InterviewPersistenceService persistenceService;

    /**
     * 创建面试会话
     */
    @PostMapping("/api/interview/sessions")
    public Result<InterviewSessionDTO> createSession(@RequestBody CreateInterviewRequest request) {
        log.info("创建面试会话，题目数量: {}", request.questionCount());
        InterviewSessionDTO session = sessionService.createSession(request);
        return Result.success(session);
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/api/interview/sessions/{sessionId}")
    public Result<InterviewSessionDTO> getSession(@PathVariable String sessionId) {
        InterviewSessionDTO session = sessionService.getSession(sessionId);
        return Result.success(session);
    }

    /**
     * 获取当前问题
     */
    @GetMapping("/api/interview/sessions/{sessionId}/question")
    public Result<Map<String, Object>> getCurrentQuestion(@PathVariable String sessionId) {
        return Result.success(sessionService.getCurrentQuestionResponse(sessionId));
    }

    /**
     * 提交答案
     */
    @PostMapping("/api/interview/sessions/{sessionId}/answers")
    public Result<SubmitAnswerResponse> submitAnswer(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> body) {
        Integer questionIndex = (Integer) body.get("questionIndex");
        String answer = (String) body.get("answer");
        log.info("提交答案: 会话{}, 问题{}", sessionId, questionIndex);
        SubmitAnswerRequest request = new SubmitAnswerRequest(sessionId, questionIndex, answer);
        SubmitAnswerResponse response = sessionService.submitAnswer(request);
        return Result.success(response);
    }

    /**
     * 生成面试报告
     */
    @GetMapping("/api/interview/sessions/{sessionId}/report")
    public Result<InterviewReportDTO> getReport(@PathVariable String sessionId) {
        log.info("生成面试报告: {}", sessionId);
        InterviewReportDTO report = sessionService.generateReport(sessionId);
        return Result.success(report);
    }

    /**
     * 查找未完成的面试会话
     */
    @GetMapping("/api/interview/sessions/unfinished/{resumeId}")
    public Result<InterviewSessionDTO> findUnfinishedSession(@PathVariable Long resumeId) {
        return Result.success(sessionService.findUnfinishedSessionOrThrow(resumeId));
    }

    /**
     * 暂存答案
     */
    @PutMapping("/api/interview/sessions/{sessionId}/answers")
    public Result<Void> saveAnswer(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> body) {
        Integer questionIndex = (Integer) body.get("questionIndex");
        String answer = (String) body.get("answer");
        log.info("暂存答案: 会话{}, 问题{}", sessionId, questionIndex);
        SubmitAnswerRequest request = new SubmitAnswerRequest(sessionId, questionIndex, answer);
        sessionService.saveAnswer(request);
        return Result.success(null);
    }

    /**
     * 提前交卷
     */
    @PostMapping("/api/interview/sessions/{sessionId}/complete")
    public Result<Void> completeInterview(@PathVariable String sessionId) {
        log.info("提前交卷: {}", sessionId);
        sessionService.completeInterview(sessionId);
        return Result.success(null);
    }

    /**
     * 获取面试会话详情
     */
    @GetMapping("/api/interview/sessions/{sessionId}/details")
    public Result<InterviewDetailDTO> getInterviewDetail(@PathVariable String sessionId) {
        InterviewDetailDTO detail = historyService.getInterviewDetail(sessionId);
        return Result.success(detail);
    }

    /**
     * 删除面试会话
     */
    @DeleteMapping("/api/interview/sessions/{sessionId}")
    public Result<Void> deleteInterview(@PathVariable String sessionId) {
        log.info("删除面试会话: {}", sessionId);
        persistenceService.deleteSessionBySessionId(sessionId);
        return Result.success(null);
    }

    /**
     * 重新评估面试
     */
    @PostMapping("/api/interview/sessions/{sessionId}/reevaluate")
    public Result<Void> reevaluateInterview(@PathVariable String sessionId) {
        log.info("重新评估面试: {}", sessionId);
        sessionService.reevaluateInterview(sessionId);
        return Result.success(null);
    }
}