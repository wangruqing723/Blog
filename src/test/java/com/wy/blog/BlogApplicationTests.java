package com.wy.blog;

import com.wy.blog.controller.BlogIndex;
import com.wy.blog.pojo.Blog;
import com.wy.blog.service.BlogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BlogApplicationTests {

    // 博客索引
    private BlogIndex blogIndex = new BlogIndex();

    @Autowired
    private BlogService blogService;

    @Test
    void contextLoads() {
        List<Blog> blogList = blogService.list(null);
        blogList.forEach(blog -> {
            try {
                //blogIndex.addIndex(blog);//添加博客索引
                //blogIndex.updateIndex(blog); // 更新博客索引
                System.out.println("blog = " + blog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

}
