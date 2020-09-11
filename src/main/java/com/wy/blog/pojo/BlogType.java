package com.wy.blog.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author WY
 * @ClassName: BlogType
 * @Description: 博客类型类
 * @date 2020/8/1
 */
@Setter
@Getter
@ToString
public class BlogType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 编号
    private String typeName; // 博客类型名称
    private Integer blogCount; // 数量
    private Integer orderNo; // 排序  从小到大排序显示

}
