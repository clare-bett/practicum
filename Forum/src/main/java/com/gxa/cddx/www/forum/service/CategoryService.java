package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.entity.Category;

import java.util.List;

/**
 * 板块服务接口
 */
public interface CategoryService {
    
    /**
     * 获取所有板块
     */
    List<Category> getAllCategories();
    
    /**
     * 根据ID获取板块
     */
    Category getCategoryById(Long categoryId);
    
    /**
     * 创建板块
     */
    Category createCategory(Category category);
    
    /**
     * 更新板块
     */
    Category updateCategory(Long categoryId, Category category);
    
    /**
     * 删除板块（软删除）
     */
    void deleteCategory(Long categoryId);
    
    /**
     * 恢复已删除的板块
     */
    Category restoreCategory(Long categoryId);
}

