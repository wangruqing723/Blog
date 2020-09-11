package com.wy.blog.service.impl;

import com.wy.blog.dao.LinkDao;
import com.wy.blog.pojo.Link;
import com.wy.blog.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 友情链接Service实现类
 *
 * @author Administrator
 */
@Service("linkService")
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkDao linkDao;

    public int add(Link link) {
        return linkDao.add(link);
    }

    public int update(Link link) {
        return linkDao.update(link);
    }

    public List<Link> list(Map<String, Object> map) {
        return linkDao.list(map);
    }

    public Long getTotal(Map<String, Object> map) {
        return linkDao.getTotal(map);
    }

    public Integer delete(Integer id) {
        return linkDao.delete(id);
    }

}
