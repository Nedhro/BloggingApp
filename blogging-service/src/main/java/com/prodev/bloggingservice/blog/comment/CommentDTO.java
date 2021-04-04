package com.prodev.bloggingservice.blog.comment;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {

    private long id;

    private Long postId;

    private Long contentId;

    private String description;

    private Date createdAt;

    private String status;

    private int totalLikes;

    private boolean like;

}
