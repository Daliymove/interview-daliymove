package com.daliymove.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daliymove.common.response.PageResult;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.entity.OperationLog;
import com.daliymove.system.entity.User;
import com.daliymove.system.mapper.OperationLogMapper;
import com.daliymove.system.mapper.UserMapper;
import com.daliymove.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;

    public PageResult<OperationLogVO> getPage(PageDTO dto) {
        Page<OperationLog> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                .like(OperationLog::getUsername, dto.getKeyword())
                .or()
                .like(OperationLog::getOperation, dto.getKeyword())
            );
        }

        if (dto.getStatus() != null) {
            wrapper.eq(OperationLog::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(OperationLog::getCreateTime);

        Page<OperationLog> logPage = operationLogMapper.selectPage(page, wrapper);

        PageResult<OperationLogVO> result = new PageResult<>();
        result.setTotal(logPage.getTotal());
        result.setSize(logPage.getSize());
        result.setCurrent(logPage.getCurrent());
        result.setPages(logPage.getPages());
        result.setRecords(logPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));

        return result;
    }

    @Async
    public void saveLog(OperationLog log) {
        if (log.getUserId() != null) {
            User user = userMapper.selectById(log.getUserId());
            if (user != null) {
                log.setUsername(user.getUsername());
            }
        }
        operationLogMapper.insert(log);
    }

    public void delete(Long id) {
        operationLogMapper.deleteById(id);
    }

    private OperationLogVO convertToVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }
}