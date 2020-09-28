package com.wy.blog.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author WY
 * @ClassName: ResponseData
 * @Description: 响应数据
 * @date 2020/8/3
 */
@Getter
@Setter
@ToString
public class UploadData {

    private String state;//返回响应信息
    private String url;//返回图片存放路径
    private String original;//返回图片名称

}
