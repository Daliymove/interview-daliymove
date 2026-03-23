package com.daliymove.admin.controller;

import com.daliymove.admin.common.PageResult;
import com.daliymove.admin.common.Result;
import com.daliymove.admin.dto.PageDTO;
import com.daliymove.admin.service.OperationLogService;
import com.daliymove.admin.vo.OperationLogVO;
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