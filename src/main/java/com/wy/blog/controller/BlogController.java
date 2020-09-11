package com.wy.blog.controller;

import com.wy.blog.pojo.Blog;
import com.wy.blog.service.BlogService;
import com.wy.blog.service.CommentService;
import com.wy.blog.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WY
 * @ClassName: BlogAdminController
 * @Description: 博客后台管理controller
 * @date 2020/8/1
 */
@Slf4j
@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    // 博客索引
    private BlogIndex blogIndex = new BlogIndex();

    /**
     * 请求博客详细信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/articles/{id}")
    public ModelAndView details(@PathVariable("id") Integer id, HttpServletRequest request) throws Exception {
        ModelAndView mv = new ModelAndView();
        Blog blog = blogService.findById(id);
        String keyWords = blog.getKeyWord();
        if (StringUtil.isNotEmpty(keyWords)) {
            String arr[] = keyWords.split(" ");
            mv.addObject("keyWords", StringUtil.filterWhite(Arrays.asList(arr)));
        } else {
            mv.addObject("keyWords", null);
        }
        mv.addObject("blog", blog);
        blog.setClickHit(blog.getClickHit() + 1); // 博客点击次数加1
        blogService.update(blog);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("blogId", blog.getId());
        map.put("state", 1); // 查询审核通过的评论
        mv.addObject("commentList", commentService.list(map));
        mv.addObject("pageCode", this.genUpAndDownPageCode(blogService.getLastBlog(id), blogService.getNextBlog(id), request.getServletContext().getContextPath()));
        mv.addObject("mainPage", "foreground/blog/view.html");
        mv.addObject("pageTitle", blog.getTitle() + "_Java个人博客系统");
        mv.setViewName("mainTemp");
        return mv;
    }

    /**
     * 根据关键字查询相关博客信息
     *
     * @param q
     * @return
     * @throws Exception
     */
    @RequestMapping("/q.do")
    public ModelAndView search(@RequestParam(value = "q", required = false) String q,
                               @RequestParam(value = "page", required = false) String page,
                               HttpServletRequest request) throws Exception {
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        log.debug("查询条件为={}", q);
        ModelAndView mv = new ModelAndView();
        mv.addObject("mainPage", "foreground/blog/result.html");
        List<Blog> blogList = blogIndex.searchBlog(q.trim());
        log.debug("查询条件筛选后的博客集合为={}", blogList);
        Integer toIndex = blogList.size() >= Integer.parseInt(page) * 10 ? Integer.parseInt(page) * 10 : blogList.size();
        mv.addObject("blogList", blogList.subList((Integer.parseInt(page) - 1) * 10, toIndex));
        log.debug("查询条件筛选后的博客集合为={}", blogList.subList((Integer.parseInt(page) - 1) * 10, toIndex));
        mv.addObject("pageCode", this.genUpAndDownPageCode(Integer.parseInt(page), blogList.size(), q, 10, request.getServletContext().getContextPath()));
        mv.addObject("q", q);
        log.debug("查询条件为={}", q);
        mv.addObject("resultTotal", blogList.size());
        log.debug("查询条件筛选后的博客集合的长度为={}", blogList.size());
        mv.addObject("pageTitle", "搜索关键字'" + q + "'结果页面_Java个人博客系统");
        mv.setViewName("mainTemp");
        return mv;
    }

    /**
     * 获取下一篇博客和下一篇博客代码
     *
     * @param lastBlog
     * @param nextBlog
     * @return
     */
    private String genUpAndDownPageCode(Blog lastBlog, Blog nextBlog, String projectContext) {
        StringBuffer pageCode = new StringBuffer();
        if (lastBlog == null || lastBlog.getId() == null) {
            pageCode.append("<p>上一篇：没有了</p>");
        } else {
            pageCode.append("<p>上一篇：<a href='" + projectContext + "/blog/articles/" + lastBlog.getId() + "'>" + lastBlog.getTitle() + "</a></p>");
        }
        if (nextBlog == null || nextBlog.getId() == null) {
            pageCode.append("<p>下一篇：没有了</p>");
        } else {
            pageCode.append("<p>下一篇：<a href='" + projectContext + "/blog/articles/" + nextBlog.getId() + "'>" + nextBlog.getTitle() + "</a></p>");
        }
        return pageCode.toString();
    }

    /**
     * 获取上一页，下一页代码 查询博客用到
     *
     * @param page           当前页
     * @param totalNum       总记录数
     * @param q              查询关键字
     * @param pageSize       每页大小
     * @param projectContext
     * @return
     */
    private String genUpAndDownPageCode(Integer page, Integer totalNum, String q, Integer pageSize, String projectContext) {
        long totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        StringBuffer pageCode = new StringBuffer();
        if (totalPage == 0) {
            return "";
        } else {
            pageCode.append("<nav>");
            pageCode.append("<ul class='pager' >");
            if (page > 1) {
                pageCode.append("<li><a href='" + projectContext + "/blog/q.do?page=" + (page - 1) + "&q=" + q + "'>上一页</a></li>");
            } else {
                pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
            }
            if (page < totalPage) {
                pageCode.append("<li><a href='" + projectContext + "/blog/q.do?page=" + (page + 1) + "&q=" + q + "'>下一页</a></li>");
            } else {
                pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
            }
            pageCode.append("</ul>");
            pageCode.append("</nav>");
        }
        return pageCode.toString();
    }
}
