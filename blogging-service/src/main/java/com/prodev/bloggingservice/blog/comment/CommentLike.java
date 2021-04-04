package com.prodev.bloggingservice.blog.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prodev.bloggingservice.model.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "comment_like")
@Data
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false, updatable = false)
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public CommentLike(User user) {
        this.user = user;
    }
}
