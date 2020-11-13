package com.wy.blog;

import com.wy.blog.lucene.BlogIndex;
import com.wy.blog.pojo.Blog;
import com.wy.blog.service.BlogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
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

    @Test
    void test2() {
        File files = new File("E:/IdeaProjects/blog/target/classes/static/userImages/");
        System.out.println("file = " + files);
        if (files.isDirectory()) {
            for (File file : files.listFiles()) {
                System.out.println("file = " + file);
                if (file.isFile()) {
                    if (!file.getName().equalsIgnoreCase("20200924032258.jpg")){
                        boolean delete = file.delete();
                        System.out.println("delete = " + delete);
                    }
                }
            }
        }

    }
}
