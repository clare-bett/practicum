package com.gxa.cddx.www.forum.controller;

import com.gxa.cddx.www.forum.annotation.RequireAuth;
import com.gxa.cddx.www.forum.dto.KnowledgeDocumentDTO;
import com.gxa.cddx.www.forum.entity.KnowledgeDocument;
import com.gxa.cddx.www.forum.service.KnowledgeDocumentService;
import com.gxa.cddx.www.forum.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    @GetMapping("/list")
    public Result<List<KnowledgeDocument>> list() {
        return Result.success(knowledgeDocumentService.findAll());
    }

    @RequireAuth(admin = true)
    @PostMapping
    public Result<KnowledgeDocument> add(@RequestBody KnowledgeDocumentDTO dto) {
        KnowledgeDocument doc = new KnowledgeDocument();
        doc.setTitle(dto.getTitle() != null ? dto.getTitle().trim() : "");
        doc.setCategory(dto.getCategory() != null ? dto.getCategory().trim() : null);
        doc.setContent(dto.getContent() != null ? dto.getContent() : "");
        return Result.success("添加成功", knowledgeDocumentService.save(doc));
    }

    @RequireAuth(admin = true)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgeDocumentService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
