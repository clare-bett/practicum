package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查询
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查询
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 判断用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 判断邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态分页查询用户
     */
    Page<User> findByStatus(Integer status, Pageable pageable);
}

