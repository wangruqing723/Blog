package com.wy.blog.controller;

import com.wy.blog.lucene.BlogIndex;
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
    private final BlogIndex blogIndex = new BlogIndex();

    /**
     * 请求博客详细信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/articles/{id}")
    public ModelAndView details(@PathVariable("id") Integer id, HttpServletRequest request) throws Exception {
        log.debug("跳转到博客详情页面id={}", id);
        ModelAndView mv = new ModelAndView();
        Blog blog = blogService.findById(id);
        if (blog == null) {
            mv.addObject("blog", null);
            mv.addObject("commentList", null);
            mv.addObject("pageCode", this.genUpAndDownPageCode(blogService.getLastBlog(id), blogService.getNextBlog(id), request.getServletContext().getContextPath()));
            mv.addObject("pageTitle", "该博客已删除");
            mv.setViewName("mainTemp");
            return mv;
        }
        blog.getReleaseDate().setTime(blog.getReleaseDate().getTime() - 28800000);
        String keyWords = blog.getKeyWord();
        if (StringUtil.isNotEmpty(keyWords)) {
            String[] arr = keyWords.split(" ");
            mv.addObject("keyWords", StringUtil.filterWhite(Arrays.asList(arr)));
        } else {
            mv.addObject("keyWords", null);
        }
        mv.addObject("blog", blog);
        // 博客点击次数加1
        blog.setClickHit(blog.getClickHit() + 1);
        blogService.update(blog);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("blogId", blog.getId());
        // 查询审核通过的评论
        map.put("state", 1);
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
        log.debug("跳转到搜索页面,搜索条件q={},page={}", q, page);
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        ModelAndView mv = new ModelAndView();
        mv.addObject("mainPage", "foreground/blog/result.html");
        List<Blog> blogList = blogIndex.searchBlog(q.trim());
        int toIndex = Math.min(blogList.size(), Integer.parseInt(page) * 10);
        mv.addObject("blogList", blogList.subList((Integer.parseInt(page) - 1) * 10, toIndex));
        mv.addObject("pageCode", this.genUpAndDownPageCode(Integer.parseInt(page), blogList.size(), q, 10, request.getServletContext().getContextPath()));
        mv.addObject("q", q);
        mv.addObject("resultTotal", blogList.size());
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
        StringBuilder pageCode = new StringBuilder();
        if (lastBlog == null || lastBlog.getId() == null) {
            pageCode.append("<p>上一篇：没有了</p>");
        } else {
            pageCode.append("<p>上一篇：<a href='").append(projectContext).append("/blog/articles/").append(lastBlog.getId()).append("'>").append(lastBlog.getTitle()).append("</a></p>");
        }
        if (nextBlog == null || nextBlog.getId() == null) {
            pageCode.append("<p>下一篇：没有了</p>");
        } else {
            pageCode.append("<p>下一篇：<a href='").append(projectContext).append("/blog/articles/").append(nextBlog.getId()).append("'>").append(nextBlog.getTitle()).append("</a></p>");
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
        StringBuilder pageCode = new StringBuilder();
        if (totalPage == 0) {
            return "";
        } else {
            pageCode.append("<nav>");
            pageCode.append("<ul class='pager' >");
            if (page > 1) {
                pageCode.append("<li><a href='").append(projectContext).append("/blog/q.do?page=").append(page - 1).append("&q=").append(q).append("'>上一页</a></li>");
            } else {
                pageCode.append("<li class='disabled'><a href='#'>上一页</a></li>");
            }
            if (page < totalPage) {
                pageCode.append("<li><a href='").append(projectContext).append("/blog/q.do?page=").append(page + 1).append("&q=").append(q).append("'>下一页</a></li>");
            } else {
                pageCode.append("<li class='disabled'><a href='#'>下一页</a></li>");
            }
            pageCode.append("</ul>");
            pageCode.append("</nav>");
        }
        return pageCode.toString();
    }
}
