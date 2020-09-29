package com.wy.blog.controller.admin;

import com.wy.blog.pojo.Blogger;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.BloggerService;
import com.wy.blog.util.BCryptPasswordEncoderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.Objects;

/**
 * @author WY
 * @ClassName: BloggerAdminController
 * @Description: 博主Controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
@RequestMapping("/admin/blogger")
public class BloggerAdminController {

    @Autowired
    private BloggerService bloggerService;

    @RequestMapping("/toModifyInfo.do")
    public String toModifyInfo(Model model) {
        Blogger blogger = bloggerService.find();
        model.addAttribute("blogger", blogger);
        log.debug("跳转到修改博主信息页面并渲染...blogger={}", blogger);
        return "admin/modifyInfo";
    }

    /**
     * 修改博主信息
     *
     * @param blogger
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public ResponseData save(Blogger blogger, ResponseData responseData) {
        int resultTotal = bloggerService.update(blogger);
        responseData.setSuccess(resultTotal > 0);
        return responseData;
    }

    /**
     * @Description: 设置完个人图像之后, 删除之前的图片, 节省空间
     * @Param: [blogger, responseData]
     * @return: com.wy.blog.pojo.ResponseData
     * @Date: 2020/9/24
     * @Author: wyong
     */
    @RequestMapping("/cleanImages.do")
    @ResponseBody
    public ResponseData cleanImages(@Param("currentImage") String currentImage, ResponseData responseData) {
        log.debug("清理图片,节省空间={}", currentImage);
        boolean delete = true;
        if (StringUtils.isNotBlank(currentImage)) {
            String filePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath().substring(1);
            // E:/IdeaProjects/blog/target/classes/
            File files = new File(filePath + "/static/userImages/");
            if (files.isDirectory()) {
                for (File file : Objects.requireNonNull(files.listFiles())) {
                    log.debug("存储图片的目录file = {}", file);
                    if (file.isFile() && !file.getName().equalsIgnoreCase(currentImage)) {
                        delete = file.delete();
                    }
                }
            }
        } else {
            responseData.setMessage("当前图片为空,没有删除!!!");
        }
        responseData.setSuccess(delete);
        return responseData;
    }

    /**
     * 查询博主信息
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping("/find.do")
    @ResponseBody
    public Blogger find() throws Exception {
        log.debug("查询博主信息");
        return bloggerService.find();
    }

    @RequestMapping("/toModifyPassword.do")
    public String toModifyPassword() {
        log.debug("跳转到修改密码页面...");
        return "admin/modifyPassword";
    }

    /**
     * 修改博主密码
     *
     * @param newPassword
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/modifyPassword.do")
    @ResponseBody
    public ResponseData modifyPassword(@RequestParam("newPassword") String newPassword, ResponseData responseData) {
        log.debug("修改密码-新密码{}", newPassword);
        Blogger blogger = new Blogger();
        blogger.setPassword(BCryptPasswordEncoderUtils.encoderPassword(newPassword));
        int resultTotal = bloggerService.update(blogger);
        responseData.setSuccess(resultTotal > 0);
        return responseData;
    }

}
