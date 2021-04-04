package com.prodev.bloggingservice.blog.post;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "blog_category")
@Data
public class PostCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

}
