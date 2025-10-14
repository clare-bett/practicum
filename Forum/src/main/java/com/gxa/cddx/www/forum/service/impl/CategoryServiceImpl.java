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
        // 检查板块名称是否已存在
        Category existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory != null) {
            // 如果是已删除的板块，提示可以恢复
            if (existingCategory.getStatus() == CommonConstant.STATUS_DISABLED) {
                throw new BusinessException("该板块名称已被使用（已删除状态），建议：1. 使用其他名称，或 2. 联系管理员恢复该板块");
            }
            // 正常状态的板块
            throw new BusinessException("板块名称已存在，请使用其他名称");
        }
        
        category.setStatus(CommonConstant.STATUS_NORMAL);
        return categoryRepository.save(category);
    }
    
    @Override
    @Transactional
    public Category updateCategory(Long categoryId, Category updateCategory) {
        Category category = getCategoryById(categoryId);
        
        if (updateCategory.getName() != null) {
            // 检查新名称是否与其他板块重复
            Category existingCategory = categoryRepository.findByName(updateCategory.getName());
            if (existingCategory != null && !existingCategory.getId().equals(categoryId)) {
                // 如果是已删除的板块，提示可以恢复
                if (existingCategory.getStatus() == CommonConstant.STATUS_DISABLED) {
                    throw new BusinessException("该板块名称已被使用（已删除状态），建议：1. 使用其他名称，或 2. 联系管理员恢复该板块");
                }
                throw new BusinessException("板块名称已存在，请使用其他名称");
            }
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
    
    @Override
    @Transactional
    public Category restoreCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "板块不存在"));
        
        if (category.getStatus() != CommonConstant.STATUS_DISABLED) {
            throw new BusinessException("该板块未被删除，无需恢复");
        }
        
        category.setStatus(CommonConstant.STATUS_NORMAL);
        return categoryRepository.save(category);
    }
}

