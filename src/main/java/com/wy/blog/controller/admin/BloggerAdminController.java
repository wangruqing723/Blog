package com.wy.blog.controller.admin;

import com.wy.blog.pojo.Blogger;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.service.BloggerService;
import com.wy.blog.util.BCryptPasswordEncoderUtils;
import com.wy.blog.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

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
        log.debug("查询博主信息并返回到页面...{}", blogger);
        model.addAttribute("blogger", blogger);
        log.debug("跳转到修改博主信息页面...");
        return "admin/modifyInfo";
    }

    /**
     * 修改博主信息
     *
     * @param imageFile
     * @param blogger
     * @param request
     * @param responseData
     * @return
     * @throws Exception
     */
    @RequestMapping("/save.do")
    @ResponseBody
    public String save(@RequestParam("file") MultipartFile imageFile, Blogger blogger,
                       HttpServletRequest request, ResponseData responseData) throws Exception {
        if (!imageFile.isEmpty()) {
//            String filePath = request.getSession().getServletContext().getRealPath("/");
            String filePath = this.getClass().getClassLoader().getResource("").getPath();
            log.debug("路径={}", filePath);
            String imageName = DateUtil.getCurrentDateStr() + "." + imageFile.getOriginalFilename().split("\\.")[1];
            log.debug("文件名={}", imageName);
            imageFile.transferTo(new File(filePath + "/static/userImages/" + imageName));
            blogger.setImageName(imageName);
        }
        int resultTotal = bloggerService.update(blogger);
        StringBuffer result = new StringBuffer();
        if (resultTotal > 0) {
            result.append("<script language='javascript'>alert('修改成功!');</script>");
            //responseData.setSuccess(true);
        } else {
            result.append("<script language='javascript'>alert('修改失败!');</script>");
            //responseData.setSuccess(false);
        }
        return result.toString();
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
        Blogger blogger = bloggerService.find();
        return blogger;
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
    public ResponseData modifyPassword(@RequestParam("newPassword") String newPassword, ResponseData responseData) throws Exception {
        Blogger blogger = new Blogger();
        blogger.setPassword(BCryptPasswordEncoderUtils.encoderPassword(newPassword));
        int resultTotal = bloggerService.update(blogger);
        if (resultTotal > 0) {
            responseData.setSuccess(true);
        } else {
            responseData.setSuccess(false);
        }
        return responseData;
    }

}
