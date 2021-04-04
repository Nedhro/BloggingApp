package com.prodev.bloggingservice.blog.comment;

import com.prodev.bloggingservice.auth.AuthUser;
import com.prodev.bloggingservice.blog.post.Post;
import com.prodev.bloggingservice.blog.post.PostRepository;
import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.repository.UserRepository;
import com.prodev.bloggingservice.service.impl.CommonServiceImpl;
import com.prodev.bloggingservice.util.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl extends CommonServiceImpl<Comment> {


    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    public CommentServiceImpl(JpaRepository<Comment, Long> repository) {
        super(repository);
    }

    @Transactional
    public Comment save(CommentDTO commentDto) throws Exception {

        Comment comment = new Comment();
        comment.setDescription(commentDto.getDescription());
        Optional<Post> post = postRepository.findById(commentDto.getPostId());
        if (post == null) {
            throw new NotFoundException(Post.class.getSimpleName() + " not found");
        }
        comment.setPost(post.get());
        this.save(comment);
        return comment;
    }

    @Transactional
    public void likeComment(long commentId, AuthUser auth) throws Exception {
        Comment comment = this.findById(commentId);
        User user = userRepository.findByUsername(auth.getUsername());
        CommentLike like = commentLikeRepository.findByCommentAndUser(comment, user);
        if (comment.getLikes().contains(like)) {
            comment.getLikes().remove(like);
            commentLikeRepository.delete(like);
        } else {
            like = new CommentLike(user);
            like.setComment(comment);
            List<CommentLike> likes = new ArrayList<>();
            likes.add(like);
            comment.setLikes(likes);
        }
        this.save(comment);
    }
}
