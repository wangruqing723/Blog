package com.wy.blog.service.impl;

import com.wy.blog.dao.BloggerDao;
import com.wy.blog.pojo.Blogger;
import com.wy.blog.service.BloggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 博主Service实现类
 *
 * @author Administrator
 */
@Service("bloggerService")
public class BloggerServiceImpl implements BloggerService {

    @Autowired
    private BloggerDao bloggerDao;

    @Override
    public Blogger find() {
        return bloggerDao.find();
    }

    @Override
    public Blogger getByUserName(String userName) {
        return bloggerDao.getByUserName(userName);
    }

    @Override
    public Integer update(Blogger blogger) {
        return bloggerDao.update(blogger);
    }

    /**
     * @Description: 利用Spring Security做登录功能
     * @Param: [s]
     * @return: org.springframework.security.core.userdetails.UserDetails
     * @Author: WY
     * @Date: 2020/2/27
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        Blogger blogger = null;
        try {
            blogger = bloggerDao.getByUserName(username);
        } catch (Exception e) {
            throw new BadCredentialsException("用户名或密码错误");
//            e.printStackTrace();
        }
        User user = new User(blogger.getUserName(), blogger.getPassword(), getAuthorities());
        return user;
    }


    private List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authority = new ArrayList<>();
        authority.add(new SimpleGrantedAuthority("ROLE_USER"));
//        authority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authority;
    }
}
