package com.wy.blog.controller.admin;

import com.wy.blog.pojo.*;
import com.wy.blog.service.BlogService;
import com.wy.blog.service.BlogTypeService;
import com.wy.blog.service.BloggerService;
import com.wy.blog.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author WY
 * @ClassName: SystemAdminController
 * @Description: 系统后台controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/admin/system")
public class SystemAdminController {

    @Autowired
    private BloggerService bloggerService;

    @Autowired
    private BlogTypeService blogTypeService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private LinkService linkService;

    @RequestMapping("/main.do")
    public String main() {
        log.debug("跳转到后台主页面...");
        return "admin/main";
    }

    @RequestMapping("/logout.do")
    public String logout() {
        log.debug("跳转到退出系统页面...");
        return "admin/logout";
    }

    @RequestMapping("/toRefreshSystem.do")
    public String toRefreshSystem() {
        log.debug("跳转到刷新系统缓存页面...");
        return "admin/refreshSystem";
    }

    /**
     * @Description: 刷新系统缓存
     * @Param: [response, request]
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/4
     */
    @RequestMapping("/refreshSystem.do")
    @ResponseBody
    public ResponseData refreshSystem(ResponseData responseData, HttpServletRequest request) throws Exception {
        ServletContext application = RequestContextUtils.findWebApplicationContext(request).getServletContext();
        // 更新博主信息
        try {
            Blogger blogger = bloggerService.find();
            blogger.setPassword(null);
            log.debug("存入app域中的博主信息为:{}", blogger);
            application.setAttribute("blogger", blogger);
            // 更新博客类型
            List<BlogType> blogTypeCountList = blogTypeService.countList();
            log.debug("存入app域中的博客类型信息为:{}", blogTypeCountList);
            application.setAttribute("blogTypeCountList", blogTypeCountList);
            // 更新博客文章
            List<Blog> blogCountList = blogService.countList();
            log.debug("存入app域中的博客信息为:{}", blogCountList);
            application.setAttribute("blogCountList", blogCountList);
            // 更新友情链接
            List<Link> linkList = linkService.list(null);
            log.debug("存入app域中的友情链接信息为:{}", linkList);
            application.setAttribute("linkList", linkList);

            responseData.setSuccess(true);
        } catch (Exception e) {
            responseData.setSuccess(false);
        }

        return responseData;
    }
}
