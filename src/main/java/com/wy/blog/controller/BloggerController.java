package com.wy.blog.controller;

import com.wy.blog.service.BloggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author WY
 * @ClassName: BloggerAdminController
 * @Description: 博主Controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/blogger")
public class BloggerController {

    @Autowired
    private BloggerService bloggerService;

    /**
     * 查找博主信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/aboutMe.do")
    public ModelAndView aboutMe() throws Exception {
        log.debug("携带数据并跳转到关于博主页面渲染");
        ModelAndView mav = new ModelAndView();
        mav.addObject("blogger", bloggerService.find());
        mav.addObject("mainPage", "foreground/blogger/info.html");
        mav.addObject("pageTitle", "关于博主_Java个人博客系统");
        mav.setViewName("mainTemp");
        return mav;
    }

}
