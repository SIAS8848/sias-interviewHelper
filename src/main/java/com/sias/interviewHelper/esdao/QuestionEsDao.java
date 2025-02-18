package com.sias.interviewHelper.esdao;

import com.sias.interviewHelper.model.dto.question.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 题目 ES 操作
 *
 * @author <a href="https://github.com/SIAS8848">程序员sias</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {

    List<QuestionEsDTO> findByUserId(Long userId);
}