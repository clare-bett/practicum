package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.constant.ResultCode;
import com.gxa.cddx.www.forum.dto.LoginDTO;
import com.gxa.cddx.www.forum.dto.RegisterDTO;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.UserService;
import com.gxa.cddx.www.forum.utils.JwtUtil;
import com.gxa.cddx.www.forum.utils.PasswordUtil;
import com.gxa.cddx.www.forum.vo.LoginVO;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.encrypt(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getNickname());
        user.setRole(CommonConstant.ROLE_USER);
        user.setStatus(CommonConstant.STATUS_NORMAL);
        
        user = userRepository.save(user);
        
        return convertToVO(user);
    }
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = userRepository.findByUsername(loginDTO.getUsername())
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        // 验证密码
        if (!PasswordUtil.verify(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.WRONG_PASSWORD, "密码错误");
        }
        
        // 检查账号状态
        if (user.getStatus() != CommonConstant.STATUS_NORMAL) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }
        
        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 返回登录信息
        UserVO userVO = convertToVO(user);
        return new LoginVO(token, userVO);
    }
    
    @Override
    public UserVO getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        return convertToVO(user);
    }
    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
    }
    
    /**
     * 转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
    
    @Override
    public PageVO<UserVO> getAllUsersForAdmin(Integer pageNum, Integer pageSize, Integer status) {
        // 设置默认值
        pageNum = (pageNum == null || pageNum < 1) ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        pageSize = Math.min(pageSize, CommonConstant.MAX_PAGE_SIZE);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询用户
        Page<User> page;
        if (status == null) {
            // 查询所有状态的用户
            page = userRepository.findAll(pageable);
        } else {
            // 查询指定状态的用户
            page = userRepository.findByStatus(status, pageable);
        }
        
        // 转换为VO
        List<UserVO> records = page.getContent().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return new PageVO<>(records, page.getTotalElements(), 
            page.getNumber() + 1, page.getSize());
    }
    
    @Override
    @Transactional
    public void adminFreezeUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        // 不允许冻结管理员
        if (CommonConstant.ROLE_ADMIN.equals(user.getRole())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "不能冻结管理员账号");
        }
        
        user.setStatus(CommonConstant.STATUS_DISABLED);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void adminUnfreezeUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        user.setStatus(CommonConstant.STATUS_NORMAL);
        userRepository.save(user);
    }
}

