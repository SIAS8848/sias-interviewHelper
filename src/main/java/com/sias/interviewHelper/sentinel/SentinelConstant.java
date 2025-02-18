package com.sias.interviewHelper.sentinel;

/**
 * Sentinel 限流熔断常量
 *
 * @author <a href="https://github.com/SIAS8848">程序员sias</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface SentinelConstant {

    /**
     * 分页获取题库列表接口限流
     */
    String listQuestionBankVOByPage = "listQuestionBankVOByPage";

    /**
     * 分页获取题目列表接口限流
     */
    String listQuestionVOByPage = "listQuestionVOByPage";
}
