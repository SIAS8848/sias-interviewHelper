package com.sias.interviewHelper.model.dto.postfavour;

import java.io.Serializable;
import lombok.Data;

/**
 * 帖子收藏 / 取消收藏请求
 *
 * @author <a href="https://github.com/SIAS8848">程序员sias</a>
 * 
 */
@Data
public class PostFavourAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}