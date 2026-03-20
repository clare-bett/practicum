package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.dto.LoginDTO;
import com.gxa.cddx.www.forum.dto.RegisterDTO;
import com.gxa.cddx.www.forum.service.UserService;
import com.gxa.cddx.www.forum.vo.LoginVO;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.Result;
import com.gxa.cddx.www.forum.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO registerDTO) {
        UserVO userVO = userService.register(registerDTO);
        return Result.success("注册成功", userVO);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }
    
    /**
     * 获取当前登录用户信息
     */
    @RequireAuth
    @GetMapping("/info")
    public Result<UserVO> getCurrentUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = userService.getUserById(userId);
        return Result.success(userVO);
    }
    
    /**
     * 管理员获取所有用户列表
     */
    @RequireAuth(admin = true)
    @GetMapping("/admin/list")
    public Result<PageVO<UserVO>> getAllUsersForAdmin(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer status) {
        PageVO<UserVO> pageVO = userService.getAllUsersForAdmin(pageNum, pageSize, status);
        return Result.success(pageVO);
    }
    
    /**
     * 管理员冻结用户
     */
    @RequireAuth(admin = true)
    @PutMapping("/admin/{userId}/freeze")
    public Result<Void> adminFreezeUser(@PathVariable Long userId) {
        userService.adminFreezeUser(userId);
        return Result.success("冻结成功", null);
    }
    
    /**
     * 管理员恢复用户
     */
    @RequireAuth(admin = true)
    @PutMapping("/admin/{userId}/unfreeze")
    public Result<Void> adminUnfreezeUser(@PathVariable Long userId) {
        userService.adminUnfreezeUser(userId);
        return Result.success("恢复成功", null);
    }
}

