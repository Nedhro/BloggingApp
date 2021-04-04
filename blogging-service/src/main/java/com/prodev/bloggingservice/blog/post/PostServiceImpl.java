package com.prodev.bloggingservice.blog.post;

import com.prodev.bloggingservice.auth.AuthUser;
import com.prodev.bloggingservice.blog.content.Content;
import com.prodev.bloggingservice.blog.content.ContentComponent;
import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.enums.Status;
import com.prodev.bloggingservice.repository.UserRepository;
import com.prodev.bloggingservice.service.impl.CommonServiceImpl;
import com.prodev.bloggingservice.util.AlreadyExistException;
import com.prodev.bloggingservice.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl extends CommonServiceImpl<Post> implements PostService {


    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BlogLikeRepository blogLikeRepository;

    @Autowired
    private ContentComponent contentComponent;

    public PostServiceImpl(JpaRepository<Post, Long> repository) {
        super(repository);
        this.postRepository = (PostRepository) repository;
    }

    @Override
    @Transactional
    public Post save(PostDTO postDto, AuthUser authenticatedUser) throws Exception {
        Post post = savePost(postDto);
        Optional<PostCategory> category = postCategoryRepository.findById(postDto.getSelectedCategory());
        if (category != null) post.setCategory(category.get());
        post.setUsers(userRepository.findByUsername(authenticatedUser.getUsername()));
        post.setTags(savePostTag(postDto, post));
        if (postDto.getImagePath() != null) {
            Content content = contentComponent.saveContent(postDto.getImagePath());
            if (content != null) post.setContent(content);
        }
        return post;
    }

    @Transactional
    Post savePost(PostDTO postDto) throws Exception {
        Post post = null;
        if (postDto.getId() != null) {
            post = this.findById(postDto.getId());
        } else {
            post = new Post();
        }
        if (post == null) {
            throw new NotFoundException(Post.class.getSimpleName() + " not found!");
        }

        String titleBn = postDto.getTitle().replaceAll(" ", "-");
        titleBn = titleBn.replaceAll("\\?", "_");

        Post postExist = this.findByTitle(titleBn);
        if (postExist != null) {
            if (!titleBn.equals(postExist.getTitle())) {
                throw new AlreadyExistException("Post already exist with this title");
            }
        }

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").parse(postDto.getCreatedAt()));
        return this.save(post);
    }

    @Transactional
    List<PostTag> savePostTag(PostDTO postDto, Post post) throws Exception {

        List<PostTag> tags = new ArrayList<>();
        for (PostTag tag : postTagRepository.findAllByPost(post)) {
            postTagRepository.delete(tag);
        }
        //post.getTags().removeAll(postTagRepository.findAllByPost(post));
        for (String str : postDto.getPostTags()) {
            str = str.replaceAll("\\[", "").replaceAll("\\]", "");
            str = str.replaceAll("\"", "");
            if (str.length() > 0 && str != null && !str.equals("null")) {
                PostTag tag = postTagRepository.findByNameAndPost(str, post);
                if (tag == null) {
                    tag = new PostTag(str);
                    tag.setPost(post);
                    postTagRepository.save(tag);
                }
            }
        }
        return tags;
    }

    @Transactional
    public PostCategory saveCategory(PostCategoryDTO dto) throws Exception {
        PostCategory category = null;
        if (dto.getId() != null) {
            category = postCategoryRepository.findById(dto.getId()).orElseThrow(() -> new NotFoundException("Category not found"));
        } else {
            category = new PostCategory();
        }
        category.setName(dto.getName());
        return postCategoryRepository.save(category);
    }

    @Override
    public List<PostCategorySummaryDTO> findPostSummaryByCategory() throws Exception {
        return postRepository.findCollectionsCategory();
    }

    @Override
    public Page<Post> findAllByStatus(Status status, int size, int page) {
        return postRepository.findAllByStatusOrderByCreatedDateDesc(status, PageRequest.of(size, page));
    }

    @Override
    public void postLike(AuthUser authUser, long postId) throws Exception {

        Post post = this.findById(postId);
        User user = userRepository.findByUsername(authUser.getUsername());
        BlogLike like = blogLikeRepository.findByPostAndUser(post, user);
        if (post.getLikes().contains(like)) {
            post.getLikes().remove(like);
            blogLikeRepository.delete(like);
        } else {
            like = new BlogLike(user);
            like.setPost(post);
            List<BlogLike> likes = new ArrayList<>();
            likes.add(like);
            post.setLikes(likes);
        }
        this.save(post);
    }

    @Override
    public BlogDTOBuilder getPostDetails(String title, AuthUser auth) throws Exception {

        Post post = this.findByTitle(title);
        if (post == null) {
            throw new NotFoundException(Post.class.getSimpleName() + " not found!");
        }
        List<PostCategorySummaryDTO> summaryDtos = this.findPostSummaryByCategory();
        List<PostTagSummaryDTO> tags = postTagRepository.getMostUseTags(PageRequest.of(0, 5));
        Page<Post> recentPosts = this.findAllByStatus(Status.PUBLISHED, 0, 5);
        BlogDTOBuilder blogBuilder = new BlogDTOBuilder()
                .withPost(post)
                .withComment(post.getComment())
                .withBlogCategory(summaryDtos)
                .withBlogTags(tags)
                .withRecentPosts(recentPosts.getContent());
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getUsername());
            BlogLike like = blogLikeRepository.findByPostAndUser(post, user);
            blogBuilder.withUser(user);
            blogBuilder.withBlogLike(like);
        }
        return blogBuilder;
    }

    @Override
    public Post findByTitle(String titleBn) throws Exception {

        return postRepository.findByTitle(titleBn);
    }

    @Override
    public Page<Post> findAllByTagName(int page, int size, String tag) throws Exception {
        return postRepository.findAllByTagName(PageRequest.of(page, size), tag);
    }

    @Override
    public Page<Post> findAllByCategoryName(int page, int size, String category) throws Exception {
        return postRepository.findAllByCategoryName(PageRequest.of(page, size), category);
    }

}
