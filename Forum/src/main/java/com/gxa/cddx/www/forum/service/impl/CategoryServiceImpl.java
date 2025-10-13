package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.constant.ResultCode;
import com.gxa.cddx.www.forum.entity.Category;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.CategoryRepository;
import com.gxa.cddx.www.forum.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 板块服务实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByStatusOrderBySortOrderDesc(CommonConstant.STATUS_NORMAL);
    }
    
    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "板块不存在"));
    }
    
    @Override
    @Transactional
    public Category createCategory(Category category) {
        category.setStatus(CommonConstant.STATUS_NORMAL);
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional
    public Category updateCategory(Long categoryId, Category updateCategory) {
        Category category = getCategoryById(categoryId);
        
        if (updateCategory.getName() != null) {
            category.setName(updateCategory.getName());
        }
        if (updateCategory.getDescription() != null) {
            category.setDescription(updateCategory.getDescription());
        }
        if (updateCategory.getIcon() != null) {
            category.setIcon(updateCategory.getIcon());
        }
        if (updateCategory.getSortOrder() != null) {
            category.setSortOrder(updateCategory.getSortOrder());
        }
        
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        category.setStatus(CommonConstant.STATUS_DISABLED);
        categoryRepository.save(category);
    }
}

