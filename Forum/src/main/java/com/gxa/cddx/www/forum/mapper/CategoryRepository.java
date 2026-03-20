package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 板块数据访问层
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据状态查询板块列表
     */
    List<Category> findByStatusOrderBySortOrderDesc(Integer status);
    
    /**
     * 根据名称查询板块
     */
    Category findByName(String name);
}

