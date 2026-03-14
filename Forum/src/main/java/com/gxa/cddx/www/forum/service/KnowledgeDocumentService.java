package com.gxa.cddx.www.forum.service;

import com.gxa.cddx.www.forum.entity.KnowledgeDocument;

import java.util.List;

public interface KnowledgeDocumentService {

    KnowledgeDocument save(KnowledgeDocument doc);

    void deleteById(Long id);

    List<KnowledgeDocument> findAll();

    List<KnowledgeDocument> searchByKeyword(String keyword, int maxResults);

    /** 用多个关键词检索并合并去重（用于“放假”扩展为“寒假、暑假”等） */
    List<KnowledgeDocument> searchByKeywords(List<String> keywords, int maxResults);
}
