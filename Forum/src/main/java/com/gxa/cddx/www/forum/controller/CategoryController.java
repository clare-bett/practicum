package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.entity.Category;
import com.gxa.cddx.www.forum.service.CategoryService;
import com.gxa.cddx.www.forum.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 板块控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有板块
     */
    @GetMapping("/list")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 根据ID获取板块
     */
    @GetMapping("/{categoryId}")
    public Result<Category> getCategoryById(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return Result.success(category);
    }
    
    /**
     * 创建板块（管理员）
     */
    @RequireAuth(admin = true)
    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        Category newCategory = categoryService.createCategory(category);
        return Result.success("创建成功", newCategory);
    }
    
    /**
     * 更新板块（管理员）
     */
    @RequireAuth(admin = true)
    @PutMapping("/{categoryId}")
    public Result<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(categoryId, category);
        return Result.success("更新成功", updatedCategory);
    }
    
    /**
     * 删除板块（管理员）
     */
    @RequireAuth(admin = true)
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success("删除成功", null);
    }
}

