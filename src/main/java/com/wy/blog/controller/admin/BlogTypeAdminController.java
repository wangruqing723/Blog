package com.wy.blog.controller.admin;

import com.wy.blog.pojo.BlogType;
import com.wy.blog.pojo.PageBean;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.BlogService;
import com.wy.blog.service.BlogTypeService;
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
 * @ClassName: BlogTypeAdminController
 * @Description: 博客类型controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/admin/blogType")
public class BlogTypeAdminController {

    @Autowired
    private BlogTypeService blogTypeService;

    @Autowired
    private BlogService blogService;

    /**
     * @Description: 跳转到博客类型管理页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/3
     */
    @RequestMapping("/toBlogTypeManage.do")
    public String commentList() {
        log.debug("跳转到博客类型管理页面...");
        return "admin/blogTypeManage";
    }

    /**
     * 分页查询博客类别信息
     *
     * @param page
     * @param rows
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ResponseData list(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                             @RequestParam(value = "limit", required = false, defaultValue = "10") String rows,
                             ResponseData responseData) throws Exception {
        log.debug("当前页:{}", page);
        log.debug("每页显示:{}", rows);
        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        List<BlogType> blogTypeList = blogTypeService.list(map);
        Long total = blogTypeService.getTotal(map);
        responseData.setData(blogTypeList);
        responseData.setCount(total);
        responseData.setCode(0);
        log.debug("返回的数据 = {}", responseData);
        return responseData;
    }

    /**
     * 添加或者修改博客类别信息
     *
     * @param blogType
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ResponseData save(BlogType blogType, ResponseData responseData) throws Exception {
        int resultTotal = 0; // 操作的记录条数
        if (blogType.getId() == null) {
            resultTotal = blogTypeService.add(blogType);
        } else {
            resultTotal = blogTypeService.update(blogType);
        }
        if (resultTotal > 0) {
            responseData.setSuccess(true);
        } else {
            responseData.setSuccess(false);
        }
        return responseData;
    }

    /**
     * 删除博客类别信息
     *
     * @param ids
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public ResponseData delete(@RequestParam(value = "ids") String ids, ResponseData responseData) throws Exception {
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {
            if (blogService.getBlogByTypeId(Integer.parseInt(idsStr[i])) > 0) {
                responseData.setExist("类型下有博客，不能删除！");
            } else {
                blogTypeService.delete(Integer.parseInt(idsStr[i]));
            }
        }
        responseData.setSuccess(true);
        return responseData;
    }

}
