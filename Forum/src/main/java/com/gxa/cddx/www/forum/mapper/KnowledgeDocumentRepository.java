package com.gxa.cddx.www.forum.mapper;

import com.gxa.cddx.www.forum.entity.KnowledgeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {

    @Query("SELECT k FROM KnowledgeDocument k WHERE k.title LIKE CONCAT('%', ?1, '%') OR k.content LIKE CONCAT('%', ?1, '%') OR (k.category IS NOT NULL AND k.category LIKE CONCAT('%', ?1, '%')) ORDER BY k.updateTime DESC")
    Page<KnowledgeDocument> searchByKeyword(String keyword, Pageable pageable);
}
