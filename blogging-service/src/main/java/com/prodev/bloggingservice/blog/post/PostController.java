package com.prodev.bloggingservice.blog.post;

import com.prodev.bloggingservice.auth.AuthUser;
import com.prodev.bloggingservice.model.enums.Status;
import com.prodev.bloggingservice.util.AlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PostController {

  /*  private static Set<String> features = new HashSet<String>();

    static {
        features.add("BLOG_COMMENT");
        features.add("BLOG_READ");
        features.add("BLOG_WRITE");
        features.add("BLOG_PUBLISH");
        features.add("BLOGCATEGORY_WRITE");
        features.add("BLOGTAG_WRITE");
        features.add("BLOGTAG_READ");
        features.add("BLOGCATEGORY_READ");
        CustomUtil.permissions.put(RoleGroup.BLOG.name(), features);
    }*/

    @Autowired
    TagRepository tagRepository;
    @Autowired
    private PostService postService;
    @Autowired
    private PostServiceImpl service;
    @Autowired
    private PostCategoryRepository postCategoryRepository;
    @Autowired
    private PostTagRepository postTagRepository;

    @GetMapping("/public/blog/post/{page}/{size}")
    public ResponseEntity<?> findAll(
            @PathVariable(value = "page") int page,
            @PathVariable(value = "size") int size,
            @RequestParam(value = "status", required = false, defaultValue = "2") int status,
            @RequestParam(value = "tag", required = false, defaultValue = "all") String tag,
            @RequestParam(value = "category", required = false, defaultValue = "all") String category) {

        try {

            Map<String, Object> map = new HashMap<>();
            Page<Post> posts = null;
            if (!"all".equalsIgnoreCase(tag)) {
                posts = postService.findAllByTagName(page, size, tag);
            } else if (!"all".equalsIgnoreCase(category)) {
                posts = postService.findAllByCategoryName(page, size, category);
            } else {
                if (status == 2) {
                    posts = postService.findAllByStatus(Status.PUBLISHED, page, size);
                } else {
                    posts = postService.findAll(page, size);
                }
            }
            map.put("posts", posts.map(this::convertToPostDTO));
            map.put("categories", postService.findPostSummaryByCategory());
            map.put("tags", popularTag());
            map.put("recents", getRecentsPosts(Status.PUBLISHED, 0, 5));
            return ResponseEntity.ok(map);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/public/blog/post/tag")
    public ResponseEntity<?> getPostsByTag(@RequestParam("tag") String tag) {
        try {
            List<PostDTO> posts = new ArrayList<>();
            for (PostTag obj : postTagRepository.findAllByName(tag)) {
                if (obj.getPost() != null) {
                    BlogDTOBuilder blogTag = new BlogDTOBuilder();
                    blogTag.withPost(obj.getPost());
                    posts.add(blogTag.build());
                }
            }
            ;

            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/public/blog/post/api/{title}")
    public ResponseEntity<?> findById(@PathVariable(value = "title") String title) {

        try {

            BlogDTOBuilder blogBuilder = postService.getPostDetails(title, null);
            return ResponseEntity.ok(blogBuilder.build());

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/admin/blog/post/api/{title}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findById(Authentication auth, @PathVariable(value = "title") String title) {

        try {
            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.UNAUTHORIZED);
            }
            BlogDTOBuilder blogBuilder = postService.getPostDetails(title, authenticatedUser);
            return ResponseEntity.ok(blogBuilder.build());

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }

    }

    @PostMapping("/admin/blog/post")
    @PreAuthorize("hasRole('Blogger')")
    public ResponseEntity<?> saveBlogPost(@ModelAttribute PostDTO postDto, Authentication auth) {

        try {

            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.UNAUTHORIZED);
            }
            postService.save(postDto, authenticatedUser);
            return ResponseEntity.ok(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/admin/post/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> adminPostLike(Authentication auth, @PathVariable(value = "id") long id) {

        try {

            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            }
            postService.postLike(authenticatedUser, id);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/admin/blog/category")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> saveBlogCategory(@RequestBody PostCategoryDTO dto, Authentication auth) {

        try {

            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            }
            PostCategory existCategory = postCategoryRepository.findByName(dto.getName());
            if (existCategory != null) {
                throw new AlreadyExistException("Category already exist");
            }
            service.saveCategory(dto);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/public/blog/category")
    public ResponseEntity<?> getAllCategory() {

        try {
            List<PostCategory> allCategory = postCategoryRepository.findAll();
            return ResponseEntity.ok(allCategory);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/admin/post/status/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> updateStatus(@PathVariable(value = "id") Long id,
                                          @RequestParam("action") String action,
                                          Authentication auth) {

        try {
            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            }

            Post post = postService.findById(id);
            if (post == null) {
                throw new EntityNotFoundException("Error! Post not found with this id");
            }
            if (action.equalsIgnoreCase("PUBLISHED")) {
                post.setStatus(Status.PUBLISHED);
            } else if (action.equalsIgnoreCase("APPROVED")) {
                post.setStatus(Status.PUBLISHED);
            } else if (action.equalsIgnoreCase("DELETED")) {
                post.setStatus(Status.DELETED);
            } else if (action.equalsIgnoreCase("DRAFT")) {
                post.setStatus(Status.DRAFT);
            }
            postService.save(post);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/public/post/title")
    public ResponseEntity<?> getTitle(@RequestParam("title") String title) {

        try {
            Post post = postService.findByTitle(title);
            if (post != null) {
                return new ResponseEntity<Post>(post, HttpStatus.OK);
            }

            return ResponseEntity.ok(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/admin/blog/tag")
    @PreAuthorize("hasRole('Blogger')")
    public ResponseEntity<?> saveBlogPostTag(@RequestBody Tag tag, Authentication auth) {

        try {
            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            }
            Tag existTag = tagRepository.findByName(tag.getName());
            if (existTag != null) {
                throw new AlreadyExistException("Tag already exist");
            }
            Tag newTag = tagRepository.save(tag);
            return ResponseEntity.ok(newTag);

        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/public/blog/tag")
    public ResponseEntity<?> getBlogTagList() {
        try {
            List<Tag> tag = tagRepository.findAll();
            return ResponseEntity.ok(tag);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    private PostDTO convertToPostDTO(final Post post) {
        PostDTO dto = new PostDTO();
        if (post.getId() != null) dto.setId(post.getId());
        if (post.getTitle() != null) dto.setTitle(post.getTitle());
        if (post.getDescription() != null) dto.setDescription(post.getDescription());
        if (post.getContent() != null) dto.setContentId(post.getContent().getId());
        if (post.getCreatedDate() != null)
            dto.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd").format(post.getCreatedDate()));
        if (post.getCategory() != null) dto.setSelectedCategory(post.getCategory().getId());
        if (post.getStatus() != null) dto.setStatus(post.getStatus().name());
        return dto;
    }

    private List<RecentPostDTO> getRecentsPosts(Status status, int page, int size) throws Exception {
        Page<Post> recentPosts = postService.findAllByStatus(status, page, size);
        List<RecentPostDTO> rPostDTO = new ArrayList<>();
        if (recentPosts.getContent() != null) {
            for (Post rpost : recentPosts.getContent()) {
                RecentPostDTO rdto = new RecentPostDTO();
                rdto.setPostId(rpost.getId());
                if (rpost.getTitle() != null) rdto.setName(rpost.getTitle());
                rPostDTO.add(rdto);
            }
        }
        return rPostDTO;
    }

    private List<String> popularTag() {
        List<String> mostUseTag = new ArrayList<>();
        for (PostTagSummaryDTO tag : postTagRepository.getMostUseTags(PageRequest.of(0, 5))) {
            if (tag.getTag() != null) {
                mostUseTag.add(tag.getTag());
            }
        }
        return mostUseTag;
    }

}
