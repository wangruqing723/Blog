package com.wy.blog.controller;

import com.wy.blog.pojo.Blog;
import com.wy.blog.pojo.Comment;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.BlogService;
import com.wy.blog.service.CommentService;
import com.wy.blog.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author WY
 * @ClassName: CommentAdminController
 * @Description: 评论controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @RequestMapping("/loadImage.do")
    public void loadImage(HttpSession session, HttpServletResponse response) {
        log.debug("刷新验证码图片...");
        try {
            ImageUtil.respImage(session, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加或者修改评论
     *
     * @param comment
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ResponseData save(Comment comment, @RequestParam("imageCode") String imageCode,
                             HttpServletRequest request, ResponseData responseData,
                             HttpSession session) throws Exception {
        log.debug("保存评论信息={},图片验证码={}", comment, imageCode);
        // 获取系统生成的验证码
        String sRand = (String) session.getAttribute("sRand");
        // 操作的记录条数
        int resultTotal = 0;
        if (!imageCode.equals(sRand)) {
            responseData.setSuccess(false);
            responseData.setMessage("验证码填写错误！");
        } else {
            // 获取用户IP
            String userIp = request.getRemoteAddr();
            comment.setUserIp(userIp);
            if (comment.getId() == null) {
                resultTotal = commentService.add(comment);
                // 该博客的回复次数加1
                Blog blog = blogService.findById(comment.getBlog().getId());
                blog.setReplyHit(blog.getReplyHit() + 1);
                blogService.update(blog);
            }
            responseData.setSuccess(resultTotal > 0);
        }
        return responseData;
    }

}
