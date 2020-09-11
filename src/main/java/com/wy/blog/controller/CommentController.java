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
            log.debug("刷新出错...{}", e);
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
        String sRand = (String) session.getAttribute("sRand"); // 获取系统生成的验证码
        int resultTotal = 0; // 操作的记录条数
        if (!imageCode.equals(sRand)) {
            responseData.setSuccess(false);
            responseData.setErrorInfo("验证码填写错误！");
        } else {
            String userIp = request.getRemoteAddr(); // 获取用户IP
            comment.setUserIp(userIp);
            if (comment.getId() == null) {
                resultTotal = commentService.add(comment);
                // 该博客的回复次数加1
                Blog blog = blogService.findById(comment.getBlog().getId());
                blog.setReplyHit(blog.getReplyHit() + 1);
                blogService.update(blog);
            } else {

            }
            if (resultTotal > 0) {
                responseData.setSuccess(true);
            } else {
                responseData.setSuccess(false);
            }
        }
        return responseData;
    }

}
