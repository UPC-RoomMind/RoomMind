package com.example.web.service;

/**
 * AI 上下文检索服务 - 基于用户问题从系统查询相关数据
 */
public interface AiContextService {

    /**
     * 根据用户问题构建检索增强的上下文
     * @param question 用户问题
     * @param userId 用户ID（可为空，为空时不查询个人数据）
     * @return 结构化的系统上下文数据（将拼入 system prompt）
     */
    String buildContext(String question, Integer userId);
}