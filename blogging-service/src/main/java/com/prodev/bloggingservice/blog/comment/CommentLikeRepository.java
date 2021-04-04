package com.prodev.bloggingservice.blog.comment;


import com.prodev.bloggingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    CommentLike findByCommentAndUser(Comment comment, User user);

}
