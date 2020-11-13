package com.wy.blog.controller.admin;

import com.wy.blog.lucene.BlogIndex;
import com.wy.blog.pojo.Blog;
import com.wy.blog.pojo.PageBean;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.BlogService;
import com.wy.blog.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping("/admin/blog")
public class BlogAdminController {

    @Autowired
    private BlogService blogService;

    @RequestMapping("/toWriteBlog.do")
    public String toWriteBlog() {
        log.debug("跳转到写博客页面...");
        return "admin/writeBlog";
    }

    // 博客索引
    private final BlogIndex blogIndex = new BlogIndex();

    /**
     * 添加或者修改博客信息
     *
     * @param blog
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ResponseData save(Blog blog, ResponseData responseData) throws Exception {
        log.debug("保存或修改博客={}", blog);
        // 操作的记录条数
        int resultTotal = 0;
        if (blog.getId() == null) {
            resultTotal = blogService.add(blog);
            // 添加博客索引
            blogIndex.addIndex(blog);
        } else {
            resultTotal = blogService.update(blog);
            // 更新博客索引
            blogIndex.updateIndex(blog);
        }
        responseData.setSuccess(resultTotal > 0);
        return responseData;
    }

    @RequestMapping("/toBlogManage.do")
    public String toBlogManage() {
        log.debug("跳转到博客管理页面...");
        return "admin/blogManage";
    }

    /**
     * 分页查询博客信息
     *
     * @param page
     * @param rows
     * @param sBlog
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ResponseData list(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                             @RequestParam(value = "rows", required = false, defaultValue = "10") String rows,
                             Blog sBlog, ResponseData responseData) {
        log.debug("后台页面搜索的参数Blog={},page={},rows={}", sBlog, page, rows);
        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("title", StringUtil.formatLike(sBlog.getTitle()));
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        List<Blog> blogList = blogService.list(map);
        log.debug("blogList-{}", blogList);
        Long total = blogService.getTotal(map);
        responseData.setCode(0);
        responseData.setData(blogList);
        responseData.setCount(total);
        return responseData;
    }

    /**
     * 删除博客信息
     *
     * @param ids
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public ResponseData delete(@RequestParam(value = "ids") String ids, ResponseData responseData) throws Exception {
        log.debug("删除博客数组ids={}", ids);
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {
            blogService.delete(Integer.parseInt(idsStr[i]));
            // 删除对应博客的索引
            blogIndex.deleteIndex(idsStr[i]);
        }
        responseData.setSuccess(true);
        return responseData;
    }

    /**
     * 通过ID查找实体
     *
     * @param id
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping("/findById.do")
    @ResponseBody
    public Blog findById(@RequestParam(value = "id") String id) throws Exception {
        log.debug("通过id查找博客id={}", id);
        return blogService.findById(Integer.parseInt(id));
    }


}
