package com.gxa.cddx.www.forum.service.impl;

import com.gxa.cddx.www.forum.constant.CommonConstant;
import com.gxa.cddx.www.forum.constant.ResultCode;
import com.gxa.cddx.www.forum.dto.PostDTO;
import com.gxa.cddx.www.forum.dto.PostUpdateDTO;
import com.gxa.cddx.www.forum.entity.Category;
import com.gxa.cddx.www.forum.entity.Post;
import com.gxa.cddx.www.forum.entity.User;
import com.gxa.cddx.www.forum.exception.BusinessException;
import com.gxa.cddx.www.forum.entity.Reply;
import com.gxa.cddx.www.forum.mapper.CategoryRepository;
import com.gxa.cddx.www.forum.mapper.PostRepository;
import com.gxa.cddx.www.forum.mapper.ReplyRepository;
import com.gxa.cddx.www.forum.mapper.UserRepository;
import com.gxa.cddx.www.forum.service.AIService;
import com.gxa.cddx.www.forum.service.PostService;
import com.gxa.cddx.www.forum.service.SensitiveWordService;
import com.gxa.cddx.www.forum.vo.PageVO;
import com.gxa.cddx.www.forum.vo.PostVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现类
 */
@Service
public class PostServiceImpl implements PostService {
    
    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @Autowired(required = false)
    private AIService aiService;
    
    @Autowired
    private SensitiveWordService sensitiveWordService;
    
    @Override
    @Transactional
    public PostVo createPost(PostDTO postDTO, Long authorId) {
        // 检查用户是否被禁言
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        if (author.getStatus() == CommonConstant.STATUS_LOCKED) {
            throw new BusinessException("您的账号已被禁言，无法发布内容");
        }
        
        // 敏感词过滤 - 检查标题和内容
        String title = postDTO.getTitle();
        String content = postDTO.getContent();
        
        if (sensitiveWordService.containsSensitiveWord(title)) {
            log.warn("🚨 检测到帖子标题包含敏感词，用户: {}, 标题: {}", authorId, title);
            throw new BusinessException("标题包含敏感词，请修改后重试");
        }
        
        if (sensitiveWordService.containsSensitiveWord(content)) {
            log.warn("🚨 检测到帖子内容包含敏感词，用户: {}, 内容前50字: {}", 
                authorId, content.substring(0, Math.min(50, content.length())));
            throw new BusinessException("内容包含敏感词，请修改后重试");
        }
        
        log.info("✅ 敏感词检查通过，发布帖子");
        
        // 检查分类是否存在
        Category category = categoryRepository.findById(postDTO.getCategoryId())
            .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "板块不存在"));
        
        // 创建帖子
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setAuthor(author);
        post.setStatus(CommonConstant.POST_NORMAL);
        
        post = postRepository.save(post);
        
        log.info("📝 用户 {} 发布帖子成功，ID: {}", authorId, post.getId());
        
        // 异步生成AI回复
        generateAIReply(post);
        
        return convertToVO(post);
    }
    
    /**
     * 生成AI回复
     */
    private void generateAIReply(Post post) {
        // 异步执行，避免阻塞主流程
        new Thread(() -> {
            try {
                if (aiService == null || !aiService.isEnabled()) {
                    return;
                }
                
                // 调用AI生成回复
                String aiReplyContent = aiService.generateReplyForPost(post);
                
                if (aiReplyContent != null && !aiReplyContent.isEmpty()) {
                    // 获取或创建AI助手用户
                    User aiUser = getOrCreateAIUser();
                    
                    // 创建AI回复（一级回复，直接回复帖子）
                    Reply reply = new Reply();
                    reply.setPost(post);
                    reply.setUser(aiUser);
                    reply.setContent(aiReplyContent);
                    reply.setParent(null);  // 明确设置为null，表示这是一级回复
                    reply.setStatus(CommonConstant.STATUS_NORMAL);
                    reply.setIsAi(1);  // 标记为AI回复
                    
                    replyRepository.save(reply);
                    
                    // 更新帖子回复数
                    postRepository.incrementReplyCount(post.getId());
                    
                    log.info("AI回复生成成功: 帖子ID={}", post.getId());
                }
            } catch (Exception e) {
                log.error("AI回复生成失败: {}", e.getMessage());
            }
        }).start();
    }
    
    /**
     * 获取或创建AI助手用户
     */
    private User getOrCreateAIUser() {
        // 先尝试查找
        User existingAI = userRepository.findByUsername("AI助手").orElse(null);
        
        if (existingAI != null) {
            return existingAI;
        }
        
        // 如果不存在，创建新用户
        User aiUser = new User();
        aiUser.setUsername("AI助手");
        aiUser.setPassword(""); // AI用户不需要密码
        aiUser.setEmail("ai@forum.com");
        aiUser.setNickname("AI智能助手");
        aiUser.setAvatar("https://img.icons8.com/color/96/artificial-intelligence.png");
        aiUser.setBio("我是AI智能助手，很高兴为您服务！");
        aiUser.setRole(CommonConstant.ROLE_USER);
        aiUser.setStatus(CommonConstant.STATUS_NORMAL);
        
        try {
            return userRepository.save(aiUser);
        } catch (Exception e) {
            // 如果保存失败（可能是并发创建），再次查询
            log.warn("创建AI用户失败，尝试重新查询");
            return userRepository.findByUsername("AI助手")
                    .orElseThrow(() -> new RuntimeException("无法获取或创建AI用户"));
        }
    }
    
    @Override
    @Transactional
    public PostVo getPostById(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ResultCode.POST_NOT_FOUND, "帖子不存在"));
        
        // 增加浏览次数
        postRepository.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);
        
        return convertToVO(post);
    }
    
    @Override
    public PageVO<PostVo> getPostList(Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Post> page = postRepository.findByStatusOrderByIsTopDescCreateTimeDesc(
            CommonConstant.POST_NORMAL, pageable);
        
        return convertToPageVO(page);
    }
    
    @Override
    public PageVO<PostVo> getPostListByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Post> page = postRepository.findByCategoryIdAndStatusOrderByIsTopDescCreateTimeDesc(
            categoryId, CommonConstant.POST_NORMAL, pageable);
        
        return convertToPageVO(page);
    }
    
    @Override
    public PageVO<PostVo> getPostListByAuthor(Long authorId, Integer pageNum, Integer pageSize) {
        pageNum = pageNum == null ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Post> page = postRepository.findByAuthorIdAndStatusOrderByCreateTimeDesc(
            authorId, CommonConstant.POST_NORMAL, pageable);
        
        return convertToPageVO(page);
    }
    
    @Override
    @Transactional
    public PostVo updatePost(Long postId, PostUpdateDTO postUpdateDTO, Long userId) {
        // 1. 查询帖子
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ResultCode.POST_NOT_FOUND, "帖子不存在"));
        
        // 2. 检查帖子状态
        if (post.getStatus() == CommonConstant.POST_DELETED) {
            throw new BusinessException("该帖子已被删除，无法编辑");
        }
        
        // 3. 获取当前用户信息
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        // 4. 权限检查：必须是作者本人或管理员
        boolean isAuthor = post.getAuthor().getId().equals(userId);
        boolean isAdmin = CommonConstant.ROLE_ADMIN.equals(currentUser.getRole());
        
        if (!isAuthor && !isAdmin) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "无权限编辑此帖子，只有作者或管理员可以编辑");
        }
        
        // 5. 检查用户是否被禁言（管理员不受此限制）
        if (!isAdmin && currentUser.getStatus() == CommonConstant.STATUS_LOCKED) {
            throw new BusinessException("您的账号已被禁言，无法编辑内容");
        }
        
        // 6. 检查是否有更新内容
        if (!postUpdateDTO.hasUpdates()) {
            throw new BusinessException("至少需要更新一项内容");
        }
        
        boolean hasChanges = false;
        
        // 7. 更新标题
        if (postUpdateDTO.getTitle() != null && !postUpdateDTO.getTitle().trim().isEmpty()) {
            String title = postUpdateDTO.getTitle().trim();
            
            // 长度验证
            if (title.length() > 100) {
                throw new BusinessException("标题长度不能超过100个字符");
            }
            
            // 敏感词检查
            if (sensitiveWordService.containsSensitiveWord(title)) {
                log.warn("🚨 检测到编辑标题包含敏感词，用户: {}, 标题: {}", userId, title);
                throw new BusinessException("标题包含敏感词，请修改后重试");
            }
            
            // 检查是否真的有变化
            if (!title.equals(post.getTitle())) {
                post.setTitle(title);
                hasChanges = true;
                log.info("📝 更新标题: {} -> {}", post.getTitle(), title);
            }
        }
        
        // 8. 更新内容
        if (postUpdateDTO.getContent() != null && !postUpdateDTO.getContent().trim().isEmpty()) {
            String content = postUpdateDTO.getContent().trim();
            
            // 长度验证
            if (content.length() > 10000) {
                throw new BusinessException("内容长度不能超过10000个字符");
            }
            
            if (content.length() < 10) {
                throw new BusinessException("内容长度不能少于10个字符");
            }
            
            // 敏感词检查
            if (sensitiveWordService.containsSensitiveWord(content)) {
                log.warn("🚨 检测到编辑内容包含敏感词，用户: {}, 内容前50字: {}", 
                    userId, content.substring(0, Math.min(50, content.length())));
                throw new BusinessException("内容包含敏感词，请修改后重试");
            }
            
            // 检查是否真的有变化
            if (!content.equals(post.getContent())) {
                post.setContent(content);
                hasChanges = true;
                log.info("📝 更新内容长度: {} -> {} 字符", 
                    post.getContent().length(), content.length());
            }
        }
        
        // 9. 更新板块
        if (postUpdateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(postUpdateDTO.getCategoryId())
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND, "板块不存在"));
            
            if (!post.getCategory().getId().equals(postUpdateDTO.getCategoryId())) {
                Category oldCategory = post.getCategory();
                post.setCategory(category);
                hasChanges = true;
                log.info("📝 更新板块: {} -> {}", oldCategory.getName(), category.getName());
            }
        }
        
        // 10. 如果没有实际变化，提示用户
        if (!hasChanges) {
            throw new BusinessException("没有检测到任何变化，无需更新");
        }
        
        // 11. 保存更新
        post = postRepository.save(post);
        
        log.info("✅ 用户 {} {} 成功编辑帖子 {}", 
            userId, 
            isAdmin ? "(管理员)" : "(作者)", 
            postId);
        
        return convertToVO(post);
    }
    
    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        // 0. 检查用户ID是否为空
        if (userId == null) {
            throw new BusinessException("用户ID不能为空，请重新登录");
        }
        
        // 1. 查询帖子
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ResultCode.POST_NOT_FOUND, "帖子不存在"));
        
        // 2. 检查帖子是否已被删除
        if (post.getStatus() == CommonConstant.POST_DELETED) {
            throw new BusinessException("该帖子已被删除");
        }
        
        // 3. 获取作者ID（处理懒加载）
        Long authorId = null;
        if (post.getAuthor() != null) {
            authorId = post.getAuthor().getId();
        }
        
        if (authorId == null) {
            throw new BusinessException("帖子作者信息异常");
        }
        
        // 4. 获取当前用户信息
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在"));
        
        // 5. 权限检查：必须是作者本人或管理员
        boolean isAuthor = authorId.equals(userId);
        boolean isAdmin = CommonConstant.ROLE_ADMIN.equals(currentUser.getRole());
        
        if (!isAuthor && !isAdmin) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "无权限删除此帖子，只有作者或管理员可以删除");
        }
        
        // 6. 检查用户是否被禁言（管理员不受此限制）
        if (!isAdmin && currentUser.getStatus() == CommonConstant.STATUS_LOCKED) {
            throw new BusinessException("您的账号已被禁言，无法删除内容");
        }
        
        // 7. 执行删除（软删除）
        post.setStatus(CommonConstant.POST_DELETED);
        postRepository.save(post);
        
        log.info("✅ 用户 {} {} 成功删除帖子 {}", 
            userId, 
            isAdmin ? "(管理员)" : "(作者)", 
            postId);
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.incrementViewCount(postId);
    }
    
    /**
     * 转换为VO
     */
    private PostVo convertToVO(Post post) {
        PostVo vo = new PostVo();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setCategoryId(post.getCategory().getId());
        vo.setCategoryName(post.getCategory().getName());
        vo.setAuthorId(post.getAuthor().getId());
        vo.setAuthorName(post.getAuthor().getUsername());
        vo.setAuthorAvatar(post.getAuthor().getAvatar());
        vo.setViewCount(post.getViewCount());
        vo.setReplyCount(post.getReplyCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setIsTop(post.getIsTop());
        vo.setIsEssence(post.getIsEssence());
        vo.setStatus(post.getStatus());
        vo.setLastReplyTime(post.getLastReplyTime());
        vo.setCreateTime(post.getCreateTime());
        return vo;
    }
    
    /**
     * 转换为分页VO
     */
    private PageVO<PostVo> convertToPageVO(Page<Post> page) {
        List<PostVo> records = page.getContent().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return new PageVO<>(records, page.getTotalElements(), 
            page.getNumber() + 1, page.getSize());
    }
    
    @Override
    public PageVO<PostVo> getAllPostsForAdmin(Integer pageNum, Integer pageSize, Integer status) {
        // 设置默认值
        pageNum = (pageNum == null || pageNum < 1) ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        pageSize = Math.min(pageSize, CommonConstant.MAX_PAGE_SIZE);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询帖子
        Page<Post> page;
        if (status == null) {
            // 查询所有状态的帖子
            page = postRepository.findAll(pageable);
        } else {
            // 查询指定状态的帖子
            page = postRepository.findByStatus(status, pageable);
        }
        
        return convertToPageVO(page);
    }
    
    @Override
    @Transactional
    public void adminDeletePost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ResultCode.POST_NOT_FOUND, "帖子不存在"));
        
        post.setStatus(CommonConstant.POST_DELETED);
        postRepository.save(post);
    }
    
    @Override
    @Transactional
    public void adminRestorePost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ResultCode.POST_NOT_FOUND, "帖子不存在"));
        
        post.setStatus(CommonConstant.POST_NORMAL);
        postRepository.save(post);
    }
    
    @Override
    public PageVO<PostVo> searchPosts(String keyword, Integer pageNum, Integer pageSize) {
        // 参数校验
        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果关键词为空，返回所有帖子
            return getPostList(pageNum, pageSize);
        }
        
        // 设置默认值
        pageNum = pageNum == null ? CommonConstant.DEFAULT_PAGE_NUM : pageNum;
        pageSize = pageSize == null ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        
        // 去除首尾空格
        String searchKeyword = keyword.trim();
        
        log.info("🔍 搜索帖子，关键词: {}, 页码: {}, 每页: {}", searchKeyword, pageNum, pageSize);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 执行搜索（标题和内容都搜索）
        Page<Post> page = postRepository.searchByTitleOrContent(
            searchKeyword, searchKeyword, CommonConstant.POST_NORMAL, pageable);
        
        log.info("✅ 搜索完成，找到 {} 条结果", page.getTotalElements());
        
        return convertToPageVO(page);
    }
}

