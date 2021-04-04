package com.prodev.bloggingservice.blog.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodev.bloggingservice.model.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "blog_like")
@Data
public class BlogLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false, updatable = false)
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public BlogLike(User user) {
        this.user = user;
    }
}
