package com.wy.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author WY
 * @ClassName: Comment
 * @Description: 评论类
 * @date 2020/8/1
 */
@Setter
@Getter
@ToString
public class Comment {

    private Integer id; // 编号
    private String userIp; // 用户IP
    private String content; // 评论内容
    private Blog blog; // 被评论的博客
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
    private Date commentDate; // 评论日期
    private Integer state; // 审核状态  0 待审核 1 审核通过 2 审核未通过

}
