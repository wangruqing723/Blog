package com.wy.blog.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author WY
 * @ClassName: Blog
 * @Description: 博客类
 * @date 2020/8/1
 */
@Setter
@Getter
@ToString
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id; // 编号
    private String title; // 博客标题
    private String summary; // 摘要
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT")
    private Date releaseDate; // 发布日期
    private Integer clickHit; // 查看次数
    private Integer replyHit; // 回复次数
    private String content; // 博客内容
    private String contentNoTag; // 博客内容 无网页标签 Lucene分词用
    private BlogType blogType; // 博客类型
    private Integer blogCount; // 博客数量 非博客实际属性，主要是 根据发布日期归档查询博客数量用
    private String releaseDateStr; // 发布日期字符串 只取年和月
    private String keyWord; // 关键字 空格隔开

    private List<String> imagesList = new LinkedList<String>(); // 博客里存在的图片 主要用于列表展示显示缩略图


}
