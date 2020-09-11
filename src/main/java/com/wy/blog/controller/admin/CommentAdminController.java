package com.wy.blog.controller.admin;

import com.wy.blog.pojo.Comment;
import com.wy.blog.pojo.PageBean;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.CommentService;
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
 * @ClassName: CommentAdminController
 * @Description: 评论controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/admin/comment")
public class CommentAdminController {

    @Autowired
    private CommentService commentService;

    /**
     * @Description: 跳转到评论管理页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/3
     */
    @RequestMapping("/toCommentManage.do")
    public String commentList() {
        log.debug("跳转到评论管理页面...");
        return "admin/commentManage";
    }

    /**
     * 分页查询评论信息
     *
     * @param page
     * @param rows
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping("/list.do")
    @ResponseBody
    public ResponseData list(@RequestParam(value = "page", required = false, defaultValue = "1") String page,
                             @RequestParam(value = "limit", required = false, defaultValue = "10") String rows,
                             @RequestParam(value = "state", required = false) String state,
                             ResponseData responseData) throws Exception {
        PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        map.put("state", state); // 评论状态
        List<Comment> commentList = commentService.list(map);
        log.debug("打印commentList = {}", commentList);
        Long total = commentService.getTotal(map);
        responseData.setData(commentList);
        responseData.setCount(total);
        responseData.setCode(0);
        log.debug("返回的数据 = {}", responseData);
        return responseData;
    }

    /**
     * 删除评论信息
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
            Integer count = commentService.delete(Integer.parseInt(idsStr[i]));
            if (count > 0) {
                responseData.setSuccess(true);
            } else {
                responseData.setSuccess(false);
            }
        }
        log.debug("删除成功={}", idsStr);
        return responseData;
    }

    /**
     * @Description: 跳转到评论审核页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/3
     */
    @RequestMapping("/toCommentReview.do")
    public String commentReview() {
        log.debug("跳转到评论审核页面...");
        return "admin/commentReview";
    }

    /**
     * @Description: 评论审核
     * @Param: [ids, state, response]
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/2/25
     */
    @RequestMapping("/review.do")
    @ResponseBody
    public ResponseData review(@RequestParam(value = "ids") String ids,
                               @RequestParam(value = "state") Integer state,
                               ResponseData responseData) throws Exception {
        String[] idsStr = ids.split(",");
        log.debug("需要审核的评论id数组 = {}", idsStr);
        for (int i = 0; i < idsStr.length; i++) {
            Comment comment = new Comment();
            comment.setState(state);
            comment.setId(Integer.parseInt(idsStr[i]));
            Integer count = commentService.update(comment);
            if (count > 0) {
                responseData.setSuccess(true);
            } else {
                responseData.setSuccess(false);
            }
        }
        log.debug("审核评论完...");
        return responseData;
    }

}
