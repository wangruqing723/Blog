package com.wy.blog.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author WY
 * @ClassName: Link
 * @Description: 友情链接类
 * @date 2020/8/1
 */
@Setter
@Getter
@ToString
public class Link {

    private Integer id; // 编号
    private String linkName; // 链接名称
    private String linkUrl; // 链接地址
    private Integer orderNo; // 排序序号 从小到大排序

}
