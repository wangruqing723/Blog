package com.wy.blog.service;

import com.wy.blog.pojo.Blogger;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 博主Service接口
 *
 * @author wy
 */
public interface BloggerService extends UserDetailsService {
    /**
     * 查询博主信息
     *
     * @return
     */
    public Blogger find();

    /**
     * 通过用户名查询用户
     *
     * @param userName
     * @return
     */
    public Blogger getByUserName(String userName);

    /**
     * 更新博主信息
     *
     * @param blogger
     * @return
     */
    public Integer update(Blogger blogger);
}
