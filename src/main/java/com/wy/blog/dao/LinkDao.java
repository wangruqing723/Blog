package com.wy.blog.dao;

import com.wy.blog.pojo.Link;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Administrator
 */
public interface LinkDao {

    /**
     * 添加友情链接
     *
     * @param link
     * @return
     */
    public int add(Link link);

    /**
     * 修改友情链接
     *
     * @param link
     * @return
     */
    public int update(Link link);

    /**
     * 查找友情链接信息
     *
     * @param map
     * @return
     */
    public List<Link> list(Map<String, Object> map);

    /**
     * 获取总记录数
     *
     * @param map
     * @return
     */
    public Long getTotal(Map<String, Object> map);

    /**
     * 删除友情链接
     *
     * @param id
     * @return
     */
    public Integer delete(Integer id);
}
