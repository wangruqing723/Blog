package com.wy.blog.controller;

import com.wy.blog.pojo.Blog;
import com.wy.blog.pojo.PageBean;
import com.wy.blog.pojo.ResponseData;
import com.wy.blog.pojo.UploadData;
import com.wy.blog.service.BlogService;
import com.wy.blog.util.DateUtil;
import com.wy.blog.util.PageUtil;
import com.wy.blog.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WY
 * @ClassName: IndexController
 * @Description: 首页Controller
 * @date 2020/8/2
 */
@Slf4j
@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    /**
     * @Description: 跳转到登录页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/2
     */
    @RequestMapping("/toLogin.do")
    public String toLogin() {
        log.debug("跳转到登录页面...");
        return "login";
    }

    /**
     * @Description: 跳转到后台首页
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/2
     */
    @RequestMapping("/main.do")
    public String main() {
        log.debug("跳转到后台首页...");
        return "admin/index";
    }

    /**
     * @Description: 跳转到错误页面
     * @Param: []
     * @return: java.lang.String
     * @Author: WY
     * @Date: 2020/8/4
     */
    @RequestMapping("/error.do")
    public String errorPage() {
        log.debug("跳转到404错误页面...");
        return "error/404";
    }

    /**
     * @Description: 初始化ueditor配置文件
     * @Param: [action, request, response]
     * @return: void
     * @Date: 2020/9/27
     * @Author: wyong
     */
    @RequestMapping("/ueditor/config.do")
    @ResponseBody
    public void initUeditor(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("初始化ueditor action={}", action);
        request.getRequestDispatcher("/ueditor/jsp/config.json").forward(request, response);
    }

    /**
     * @Description: 上传图片并返回存储路径
     * @Param: [imageFile, request]
     * @return: com.wy.blog.pojo.UploadData
     * @Date: 2020/9/27
     * @Author: wyong
     */
    @RequestMapping("/ueditor/upload.do")
    @ResponseBody
    public UploadData upload(@RequestParam("file") MultipartFile imageFile, HttpServletRequest request) {
        log.debug("ueditor上传图片,因为ueditor回显图片需要用到网络虚拟路径访问图片,所以需要返回访问路径");
        UploadData uploadData = null;
        try {
            uploadData = new UploadData();
            //获取url和uri,得到服务器虚拟路径
            // http://localhost:8109/ueditor/config.do
            StringBuffer imageUrl = request.getRequestURL();
            // /ueditor/config
            String imageUri = request.getRequestURI();
            //获取系统时间生成存储图片的路径和名称
            Date date = new Date(System.currentTimeMillis());
            String format = new SimpleDateFormat("yyyyMMdd").format(date);
            // 使用uri为切割条件,把url切割之后得到 http://localhost:8109/
            String path = (imageUrl.toString().split(imageUri))[0] + "/userImages/" + format + "/";
            //获取文件的类型 image/png   image/jpg   切割之后拼接成文件名
            String imageName = date.getTime() + "." + Objects.requireNonNull(imageFile.getContentType()).split("/")[1];
            //访问路径
            String accessPath = path + imageName;
            log.debug("访问路径accessPath={}", accessPath);
            //存储路径
            String storagePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath().substring(1) + "/static/userImages/" + format + "/" + imageName;
            log.debug("存储路径storagePath={}", storagePath);

            //创建图片存放目录
            FileUtils.forceMkdir(new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath().substring(1) + "/static/userImages/" + format));

            //使用io流存储文件
            imageFile.transferTo(new File(storagePath));

            uploadData.setState("SUCCESS");
            uploadData.setOriginal(imageName);
            uploadData.setUrl(accessPath);
        } catch (IOException e) {
            uploadData.setState("文件上传失败");
            e.printStackTrace();
        }
        return uploadData;
    }

    /**
     * @Description: 上传图片
     * @Param: [imageFile, responseData]
     * @return: com.wy.blog.pojo.ResponseData
     * @Date: 2020/9/27
     * @Author: wyong
     */
    @RequestMapping("/upload.do")
    @ResponseBody
    public ResponseData upload(@RequestParam("file") MultipartFile imageFile, ResponseData responseData) throws Exception {
        log.debug("上传图片并返回存储(访问)路径,因为不需要回显,所以不需要访问路径,只需返回存储路径");
        String filePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath().substring(1);
//        E:/IdeaProjects/blog/target/classes/
        String imageName = DateUtil.getCurrentDateStr() + "." + Objects.requireNonNull(imageFile.getOriginalFilename()).split("\\.")[1];
        String pathname = filePath + "/static/userImages/" + imageName;
        log.debug("pathname={}", pathname);
        imageFile.transferTo(new File(pathname));
        Map<String, String> map = new HashMap<>(16);
        map.put("imageName", imageName);
        map.put("pathname", pathname);
        responseData.setData(map);
        return responseData;
    }

    /**
     * 请求主页
     *
     * @return
     * @throws Exception
     */
    @RequestMapping({"/", "/index", "/index.do"})
    public ModelAndView index(@RequestParam(value = "page", required = false) String page,
                              @RequestParam(value = "typeId", required = false) String typeId,
                              @RequestParam(value = "releaseDateStr", required = false) String releaseDateStr,
                              HttpServletRequest request) {
        log.debug("博客首页参数-page={},typeId={},releaseDateStr={}", page, typeId, releaseDateStr);
        ModelAndView mv = new ModelAndView();
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        PageBean pageBean = new PageBean(Integer.parseInt(page), 10);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("start", pageBean.getStart());
        map.put("size", pageBean.getPageSize());
        map.put("typeId", typeId);
        map.put("releaseDateStr", releaseDateStr);
        List<Blog> blogList = blogService.list(map);
        log.debug("设置图片之前的blogList={}", blogList);
        blogList.forEach(blog -> {
            blog.getReleaseDate().setTime(blog.getReleaseDate().getTime() - 28800000);
            List<String> imagesList = blog.getImagesList();
            String blogInfo = blog.getContent();
            Document doc = Jsoup.parse(blogInfo);
            //　查找扩展名是jpg的图片
            Elements jpgs = doc.select("img[src$=.jpg]");
            for (Element jpg : jpgs) {
                imagesList.add(jpg.toString());
                if (imagesList.size() == 3) {
                    break;
                }
            }
            //　查找扩展名是png的图片
            if (CollectionUtils.isEmpty(imagesList) || imagesList.size() < 3) {
                Elements pngs = doc.select("img[src$=.png]");
                for (Element png : pngs) {
                    imagesList.add(png.toString());
                    if (imagesList.size() == 3) {
                        break;
                    }
                }
            }
            //　查找扩展名是gif的图片
            if (CollectionUtils.isEmpty(imagesList) || imagesList.size() < 3) {
                Elements gifs = doc.select("img[src$=.gif]");
                for (Element gif : gifs) {
                    imagesList.add(gif.toString());
                    if (imagesList.size() == 3) {
                        break;
                    }
                }
            }

        });
        log.debug("设置图片之后的blogList={}", blogList);
        mv.addObject("blogList", blogList);
        // 查询参数
        StringBuilder param = new StringBuilder();
        if (StringUtil.isNotEmpty(typeId)) {
            param.append("typeId=").append(typeId).append("&");
        }
        if (StringUtil.isNotEmpty(releaseDateStr)) {
            param.append("releaseDateStr=").append(releaseDateStr).append("&");
        }
        mv.addObject("pageCode", PageUtil.genPagination(request.getContextPath() + "/index.do", blogService.getTotal(map), Integer.parseInt(page), 10, param.toString()));
        mv.addObject("mainPage", "foreground/blog/list.html");
        mv.addObject("pageTitle", "Java个人博客系统");
        mv.setViewName("mainTemp");
        return mv;
    }

}
