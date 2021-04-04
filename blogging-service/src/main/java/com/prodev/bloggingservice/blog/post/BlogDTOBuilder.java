package com.prodev.bloggingservice.blog.post;

import com.prodev.bloggingservice.blog.comment.Comment;
import com.prodev.bloggingservice.blog.comment.CommentDTO;
import com.prodev.bloggingservice.blog.comment.CommentLike;
import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.enums.Status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BlogDTOBuilder {

    private Post post;
    private Collection<Comment> comments;
    private BlogLike like;
    private List<PostCategorySummaryDTO> categories;
    private List<PostTagSummaryDTO> popularTags;
    private List<Post> recents;
    private User user;

    public BlogDTOBuilder withPost(Post post) {
        this.post = post;
        return this;
    }

    public BlogDTOBuilder withBlogLike(BlogLike like) {
        this.like = like;
        return this;
    }

    public BlogDTOBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    public BlogDTOBuilder withBlogCategory(List<com.prodev.bloggingservice.blog.post.PostCategorySummaryDTO> categories) {
        this.categories = categories;
        return this;
    }

    public BlogDTOBuilder withBlogTags(List<com.prodev.bloggingservice.blog.post.PostTagSummaryDTO> tags) {
        this.popularTags = tags;
        return this;
    }

    public BlogDTOBuilder withRecentPosts(List<Post> posts) {
        this.recents = posts;
        return this;
    }

    public BlogDTOBuilder withComment(Collection<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public com.prodev.bloggingservice.blog.post.PostDTO build() throws Exception {

        com.prodev.bloggingservice.blog.post.PostDTO dto = new com.prodev.bloggingservice.blog.post.PostDTO();
        if (post != null) {
            if (post.getId() != null) dto.setId(post.getId());
            if (post.getTitle() != null) dto.setTitle(post.getTitle());
            if (post.getDescription() != null) dto.setDescription(post.getDescription());
            if (post.getContent() != null) dto.setContentId(post.getContent().getId());
            if (post.getCreatedDate() != null) {
                dto.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd").format(post.getCreatedDate()));
            }
            if (post.getCategory() != null) dto.setSelectedCategory(post.getCategory().getId());
            if (post.getStatus() != null) dto.setStatus(post.getStatus().name());
            if (post.getCategory() != null) dto.setSelectedCategory(post.getCategory().getId());
            if (post.getTags().size() > 0) dto.setPostTags(post.getTags().stream()
                    .map(com.prodev.bloggingservice.blog.post.PostTag::getTags)
                    .collect(Collectors.toList()));
            dto.setLikes(post.getLikes().size());
        }

        if (comments != null) {
            List<CommentDTO> dtos = new ArrayList<>();
            for (Comment c : comments) {
                if (c.getStatus() != Status.DELETED) {
                    CommentDTO cdto = new CommentDTO();
                    if (c.getId() != null) cdto.setId(c.getId());
                    if (c.getDescription() != null) cdto.setDescription(c.getDescription());
                    if (c.getContent() != null) cdto.setContentId(c.getContent().getId());
                    if (c.getCreatedDate() != null) cdto.setCreatedAt(c.getCreatedDate());
                    if (c.getStatus() != null) cdto.setStatus(c.getStatus().name());
                    cdto.setTotalLikes(c.getLikes().size());
                    if (user != null) {
                        for (CommentLike like : c.getLikes()) {
                            if (like.getUser().getId() == user.getId()) cdto.setLike(true);//TODO
                        }
                    }
                    dtos.add(cdto);
                }
            }
            dto.setComments(dtos);
        }

        if (like != null) {
            dto.setLike(true);
        }

        if (categories != null) {
            dto.setCategories(categories);
        }

        if (popularTags != null) {
            List<String> selTag = new ArrayList<>();
            for (com.prodev.bloggingservice.blog.post.PostTagSummaryDTO tag : popularTags) {
                if (tag.getTag() != null) {
                    selTag.add(tag.getTag());
                }
            }
            dto.setPopularTags(selTag);
        }

        if (recents != null) {
            List<com.prodev.bloggingservice.blog.post.RecentPostDTO> rPostDTO = new ArrayList<>();
            for (Post rpost : recents) {
                com.prodev.bloggingservice.blog.post.RecentPostDTO rdto = new com.prodev.bloggingservice.blog.post.RecentPostDTO();
                rdto.setPostId(rpost.getId());
                if (rpost.getTitle() != null) rdto.setName(rpost.getTitle());
                rPostDTO.add(rdto);
            }
            dto.setRecentsPosts(rPostDTO);
        }
        return dto;
    }

}
