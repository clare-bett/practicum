package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 举报Repository
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * 根据状态分页查询举报
     */
    Page<Report> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 查询待处理的举报数量
     */
    long countByStatus(Integer status);
}

