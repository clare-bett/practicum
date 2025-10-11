package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.dto.LoginDTO;
import com.gxa.cddx.www.forum.dto.RegisterDTO;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.vo.LoginVO;
import com.gxa.cddx.www.forum.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    UserVO register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);
    
    /**
     * 根据ID获取用户信息
     */
    UserVO getUserById(Long userId);
    
    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);
    
    /**
     * 管理员获取所有用户列表（分页）
     */
    com.gxa.cddx.www.forum.vo.PageVO<UserVO> getAllUsersForAdmin(Integer pageNum, Integer pageSize, Integer status);
    
    /**
     * 管理员冻结用户
     */
    void adminFreezeUser(Long userId);
    
    /**
     * 管理员恢复用户
     */
    void adminUnfreezeUser(Long userId);
}

