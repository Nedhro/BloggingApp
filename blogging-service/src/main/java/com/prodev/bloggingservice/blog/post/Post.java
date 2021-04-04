package com.prodev.bloggingservice.blog.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodev.bloggingservice.blog.comment.Comment;
import com.prodev.bloggingservice.blog.content.Content;
import com.prodev.bloggingservice.model.BaseModel;
import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.enums.Status;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "blog_post")
@Data
public class Post extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", unique = true)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @OneToOne
    private Content content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User users;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @OrderBy("createdDate")
    private Collection<Comment> comment;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Collection<com.prodev.bloggingservice.blog.post.BlogLike> likes = new ArrayList<>();

    @OneToOne
    private PostCategory category;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostTag> tags = new ArrayList<>();

    private Status status = Status.DRAFT;
}
