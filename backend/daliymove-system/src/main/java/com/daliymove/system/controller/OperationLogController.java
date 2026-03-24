package com.daliymove.system.controller;

import com.daliymove.common.response.PageResult;
import com.daliymove.common.response.Result;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.service.OperationLogService;
import com.daliymove.system.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public Result<PageResult<OperationLogVO>> getPage(PageDTO dto) {
        return Result.success(operationLogService.getPage(dto));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operationLogService.delete(id);
        return Result.success();
    }
}