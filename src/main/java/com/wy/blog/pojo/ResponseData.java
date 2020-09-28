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
public class ResponseData {

    private Object data;    //返回的数据
    private Long count;     //返回总记录数
    private Integer code;   //返回码
    private Boolean success;//返回状态
    private String exist;    //返回是否存在状态
    private String message;//返回响应信息

}
