package com.wy.blog.config;

import com.wy.blog.service.BloggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author WY
 * @ClassName: SecurityConfig
 * @Description: Security配置类
 * @date 2020/8/2
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BloggerService bloggerService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 认证
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(bloggerService).passwordEncoder(new BCryptPasswordEncoder());

    }

    /**
     * 授权
     * 自定义请求授权访问规则
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/index", "/login.do", "/index.do", "/toLogin.do", "/blog/**", "/blogger/**", "/comment/**", "/foreground/**", "/bootstrap3/**", "/css/**", "/images/**", "/layuimini/**", "/ueditor/**", "/userImages/**", "/favicon.ico").permitAll()
                .anyRequest().authenticated();//除了上面的资源,其余的资源需要认证才能访问

        //loginPage: 认证错误或没有认证的所跳转的页面   loginProcessingUrl:登录提交的路径,不指定默认为login
        http.formLogin().loginPage("/toLogin.do").loginProcessingUrl("/login.do")
                .usernameParameter("userName").passwordParameter("password")
                .defaultSuccessUrl("/main.do").successForwardUrl("/main.do");

        //关闭跨域保护
        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

        http.logout().logoutSuccessUrl("/toLogin.do");

        //自定义异常处理器
        http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                               AccessDeniedException e) throws IOException, ServletException {
                /*if (("Bad credentials").equals(e.getMessage())) {
                    request.setAttribute("message", "密码错误");
                    request.getRequestDispatcher("/WEB-INF/login.jsp");
                }*/
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    //ajax请求
                    String msg = "403";
                    response.getWriter().write(msg);
                } else {
                    //普通请求
                    request.getRequestDispatcher("/error.do").forward(request, response);
                }
            }
        });

    }
}
