package com.prodev.bloggingservice.blog.post;


import com.prodev.bloggingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogLikeRepository extends JpaRepository<com.prodev.bloggingservice.blog.post.BlogLike, Long> {

    com.prodev.bloggingservice.blog.post.BlogLike findByPostAndUser(Post post, User user);

}
