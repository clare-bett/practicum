package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.entity.KnowledgeDocument;
import com.gxa.cddx.www.forum.mapper.KnowledgeDocumentRepository;
import com.gxa.cddx.www.forum.service.KnowledgeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KnowledgeDocumentServiceImpl implements KnowledgeDocumentService {

    @Autowired
    private KnowledgeDocumentRepository knowledgeDocumentRepository;

    /** 中文短词长度，用于提高检索命中（整句 LIKE 常匹配不到） */
    private static final int SHORT_KEYWORD_LEN = 2;

    @Override
    @Transactional
    public KnowledgeDocument save(KnowledgeDocument doc) {
        return knowledgeDocumentRepository.save(doc);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        knowledgeDocumentRepository.deleteById(id);
    }

    @Override
    public List<KnowledgeDocument> findAll() {
        return knowledgeDocumentRepository.findAll();
    }

    @Override
    public List<KnowledgeDocument> searchByKeyword(String keyword, int maxResults) {
        if (keyword == null || keyword.trim().isEmpty()) return List.of();
        String q = keyword.trim();
        if (q.isEmpty()) return List.of();
        Set<Long> seen = new HashSet<>();
        List<KnowledgeDocument> result = new ArrayList<>();
        List<String> keywords = buildSearchKeywords(q);
        for (String k : keywords) {
            List<KnowledgeDocument> page = knowledgeDocumentRepository.searchByKeyword(k, PageRequest.of(0, maxResults * 2)).getContent();
            for (KnowledgeDocument d : page) {
                if (seen.add(d.getId())) {
                    result.add(d);
                    if (result.size() >= maxResults) break;
                }
            }
            if (result.size() >= maxResults) break;
        }
        return result;
    }

    /**
     * 生成检索关键词：整句 LIKE 常匹配不到，改为用短词（如「校历」「开学」）多次检索并合并。
     * 顺序：先整句(截断)、再前4字、再前2字，再按每2字切分去重。
     */
    private List<String> buildSearchKeywords(String query) {
        Set<String> set = new LinkedHashSet<>();
        if (query.length() > 80) query = query.substring(0, 80);
        set.add(query);
        if (query.length() >= 4) set.add(query.substring(0, 4));
        if (query.length() >= SHORT_KEYWORD_LEN) set.add(query.substring(0, SHORT_KEYWORD_LEN));
        for (int i = 0; i <= query.length() - SHORT_KEYWORD_LEN; i++) {
            set.add(query.substring(i, i + SHORT_KEYWORD_LEN));
        }
        return set.stream().filter(s -> s != null && !s.isEmpty()).limit(8).collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeDocument> searchByKeywords(List<String> keywords, int maxResults) {
        if (keywords == null || keywords.isEmpty()) return List.of();
        Set<Long> seen = new HashSet<>();
        List<KnowledgeDocument> result = new ArrayList<>();
        for (String k : keywords) {
            if (k == null || k.trim().isEmpty()) continue;
            List<KnowledgeDocument> page = searchByKeyword(k.trim(), maxResults * 2);
            for (KnowledgeDocument d : page) {
                if (seen.add(d.getId())) {
                    result.add(d);
                    if (result.size() >= maxResults) return result;
                }
            }
        }
        return result;
    }
}
