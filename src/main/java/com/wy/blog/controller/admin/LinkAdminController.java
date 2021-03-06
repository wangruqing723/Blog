package com.wy.blog.controller.admin;

import com.wy.blog.pojo.Link;
import com.wy.blog.pojo.PageBean;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.LinkService;
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
 * @ClassName: LinkAdminController
 * @Description: 友情链接controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/admin/link")
public class LinkAdminController {

    /**
     * @Description: 跳转到友情链接管理页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/4
     */
    @RequestMapping("/toLinkManage.do")
    public String toLinkManage() {
        log.debug("跳转到友情链接管理页面...");
        return "admin/linkManage";
    }

    @Autowired
    private LinkService linkService;

    /**
     * 分页查询友情链接信息
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
                             @RequestParam(value = "rows", required = false, defaultValue = "10") String rows,
                             ResponseData responseData) {
        log.debug("分页查询友情链接参数page={},rows={}", page, rows);
        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        List<Link> linkList = linkService.list(map);
        Long total = linkService.getTotal(map);
        responseData.setData(linkList);
        responseData.setCount(total);
        responseData.setCode(0);
        return responseData;
    }

    /**
     * 添加或者修改友情链接信息
     *
     * @param link
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ResponseData save(Link link, ResponseData responseData) {
        log.debug("更新友情链接link={}", link);
        // 操作的记录条数
        int resultTotal = 0;
        if (link.getId() == null) {
            resultTotal = linkService.add(link);
        } else {
            resultTotal = linkService.update(link);
        }
        responseData.setSuccess(resultTotal > 0);
        return responseData;
    }

    /**
     * 删除友情链接信息
     *
     * @param ids
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/delete.do")
    @ResponseBody
    public ResponseData delete(@RequestParam(value = "ids") String ids, ResponseData responseData) {
        log.debug("删除友情链接ids={}", ids);
        String[] idsStr = ids.split(",");
        for (String s : idsStr) {
            linkService.delete(Integer.parseInt(s));
        }
        responseData.setSuccess(true);
        return responseData;
    }

}
