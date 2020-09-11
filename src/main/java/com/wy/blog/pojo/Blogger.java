package com.wy.blog.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author WY
 * @ClassName: Blogger
 * @Description: 博主类
 * @date 2020/8/1
 */
@Setter
@Getter
@ToString
public class Blogger {

    private Integer id; // 编号
    private String userName; // 用户名
    private String password; // 密码
    private String nickName; // 昵称
    private String sign; // 个性签名
    private String editorValue; // 个人简介
    private String imageName; // 博主头像

}
