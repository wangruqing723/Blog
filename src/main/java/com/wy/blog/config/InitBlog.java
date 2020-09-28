package com.wy.blog.config;

import com.wy.blog.pojo.Blog;
import com.wy.blog.pojo.BlogType;
import com.wy.blog.pojo.Blogger;
import com.wy.blog.pojo.Link;
import com.wy.blog.service.BlogService;
import com.wy.blog.service.BlogTypeService;
import com.wy.blog.service.BloggerService;
import com.wy.blog.service.LinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @author WY
 * @ClassName: InitBlog
 * @Description: 初始化博客
 * @date 2020/8/9
 */
@Slf4j
@Component
public class InitBlog implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //获取applicationContext
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        //获取application
        ServletContext application = ((WebApplicationContext) contextRefreshedEvent.getApplicationContext()).getServletContext();
        assert application != null;

        BloggerService bloggerService = (BloggerService) applicationContext.getBean("bloggerService");
        // 查询博主信息
        Blogger blogger = bloggerService.find();
        blogger.setPassword(null);
        log.debug("存入app域中的博主信息为:{}", blogger);
        application.setAttribute("blogger", blogger);

        BlogTypeService blogTypeService = (BlogTypeService) applicationContext.getBean("blogTypeService");
        // 查询博客类别以及博客的数量
        List<BlogType> blogTypeCountList = blogTypeService.countList();
        log.debug("存入app域中的博客类型信息为:{}", blogTypeCountList);
        application.setAttribute("blogTypeCountList", blogTypeCountList);

        BlogService blogService = (BlogService) applicationContext.getBean("blogService");
        // 根据日期分组查询博客
        List<Blog> blogCountList = blogService.countList();
        log.debug("根据日期分组查询博客,存入app域中的博客信息为:{}", blogCountList);
        application.setAttribute("blogCountList", blogCountList);

        LinkService linkService = (LinkService) applicationContext.getBean("linkService");
        // 查询所有的友情链接信息
        List<Link> linkList = linkService.list(null);
        log.debug("存入app域中的友情链接信息为:{}", linkList);
        application.setAttribute("linkList", linkList);
    }
}
