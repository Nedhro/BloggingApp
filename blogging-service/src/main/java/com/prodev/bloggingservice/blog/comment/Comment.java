package com.prodev.bloggingservice.blog.comment;

import com.prodev.bloggingservice.blog.content.Content;
import com.prodev.bloggingservice.blog.post.Post;
import com.prodev.bloggingservice.model.BaseModel;
import com.prodev.bloggingservice.model.enums.Status;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "blog_comment")
@Data
public class Comment extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false, updatable = false)
    private Long id;

    @Lob
    @Column(name = "description")
    private String description;


    private Status status = Status.DRAFT;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private Collection<CommentLike> likes = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Content content;
}
