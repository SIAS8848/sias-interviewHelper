package com.sias.interviewHelper.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建题库请求
 *
 * @author <a href="https://github.com/SIAS8848">程序员sias</a>
 * 
 */
@Data
public class QuestionBankAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    private static final long serialVersionUID = 1L;
}