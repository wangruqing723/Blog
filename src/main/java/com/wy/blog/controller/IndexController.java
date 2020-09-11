package com.wy.blog.controller;

import com.baidu.ueditor.ActionEnter;
import com.wy.blog.pojo.Blog;
import com.wy.blog.pojo.PageBean;
import com.wy.blog.service.BlogService;
import com.wy.blog.util.PageUtil;
import com.wy.blog.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WY
 * @ClassName: IndexController
 * @Description: 首页Controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    /**
     * @Description: 跳转到前台首页
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/2
     */
    /*@RequestMapping({"/","/index"})
    public String index() {
        log.debug("跳转到前台首页...");
        return "index";
    }*/

    /**
     * @Description: 跳转到登录页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/2
     */
    @RequestMapping("/toLogin.do")
    public String toLogin() {
        log.debug("跳转到登录页面...");
        return "login";
    }

    /**
     * @Description: 跳转到后台首页
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/2
     */
    @RequestMapping("/main.do")
    public String main() {
        log.debug("跳转到后台首页...");
        return "admin/index";
    }

    /**
     * @Description: 跳转到错误页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/4
     */
    @RequestMapping("/error.do")
    public String errorPage() {
        log.debug("跳转到错误页面...");
        return "error/404";
    }

    /*@RequestMapping("/ueditor/config.do")
    public String ueditorConfig(String action){
        if (action.equals("uploadimage")) {
            return "";
        }
        log.debug("加载ueditor配置文件...");
        return "redirect:/ueditor/jsp/config.json";
    }*/

    @RequestMapping("/ueditor/config.do")
    public String upload(HttpServletRequest request, HttpServletResponse response, String action) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "text/html");
        //String ueditorUrl = request.getSession().getServletContext().getRealPath("/");
        String ueditorUrl = this.getClass().getClassLoader().getResource("").getPath();
        log.debug("路径={}", ueditorUrl);
        String result = new ActionEnter(request, ueditorUrl).exec();
        ueditorUrl = ueditorUrl.replaceAll("\\\\", "/");
        log.debug("action={}", action);
        if (action != null && (action.equals("listfile") || action.equals("listimage") || action.equals("uploadimage"))) {
            return result/*.replaceAll(ueditorUrl, "")*/;
        } else {
            return "redirect:/ueditor/jsp/config.json";
        }
    }

    /**
     * 请求主页
     *
     * @return
     * @throws Exception
     */
    @RequestMapping({"/", "/index", "/index.do"})
    public ModelAndView index(@RequestParam(value = "page", required = false) String page,
                              @RequestParam(value = "typeId", required = false) String typeId,
                              @RequestParam(value = "releaseDateStr", required = false) String releaseDateStr,
                              HttpServletRequest request) throws Exception {
        ModelAndView mv = new ModelAndView();
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        PageBean pageBean = new PageBean(Integer.parseInt(page), 10);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        map.put("typeId", typeId);
        map.put("releaseDateStr", releaseDateStr);
        List<Blog> blogList = blogService.list(map);
        for (Blog blog : blogList) {
            List<String> imagesList = blog.getImagesList();
            String blogInfo = blog.getContent();
            Document doc = Jsoup.parse(blogInfo);
            Elements jpgs = doc.select("img[src$=.jpg]"); //　查找扩展名是jpg的图片
            for (int i = 0; i < jpgs.size(); i++) {
                Element jpg = jpgs.get(i);
                imagesList.add(jpg.toString());
                if (i == 2) {
                    break;
                }
            }
        }
        mv.addObject("blogList", blogList);
        StringBuffer param = new StringBuffer(); // 查询参数
        if (StringUtil.isNotEmpty(typeId)) {
            param.append("typeId=" + typeId + "&");
        }
        if (StringUtil.isNotEmpty(releaseDateStr)) {
            param.append("releaseDateStr=" + releaseDateStr + "&");
        }
        mv.addObject("pageCode", PageUtil.genPagination(request.getContextPath() + "/index.do", blogService.getTotal(map), Integer.parseInt(page), 10, param.toString()));
        mv.addObject("mainPage", "foreground/blog/list.html");
        mv.addObject("pageTitle", "Java个人博客系统");
        mv.setViewName("mainTemp");
        return mv;
    }

}
