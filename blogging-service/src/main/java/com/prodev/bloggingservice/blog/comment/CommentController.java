package com.prodev.bloggingservice.blog.comment;

import com.prodev.bloggingservice.annotations.ApiController;
import com.prodev.bloggingservice.auth.AuthUser;
import com.prodev.bloggingservice.model.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@ApiController
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;


    @PostMapping("/admin/blog/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> saveComment(@ModelAttribute CommentDTO commentDto) {

        try {
            commentService.save(commentDto);
            return ResponseEntity.ok(commentDto);
        } catch (Exception e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping("/admin/comment/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> commentLike(Authentication auth, @PathVariable(value = "id") long id) {

        try {

            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.UNAUTHORIZED);
            }
            commentService.likeComment(id, authenticatedUser);
            ;
            return ResponseEntity.ok(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/admin/comment/status/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> updateStatus(@PathVariable(value = "id") Long id, @RequestParam("action") String action, Authentication auth) {

        try {
            AuthUser authenticatedUser = (AuthUser) auth.getPrincipal();
            if (authenticatedUser == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
            }

            Comment comment = commentService.findById(id);
            if (comment == null) {
                return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
            }
            if (action.equalsIgnoreCase("PUBLISHED")) {
                comment.setStatus(Status.PUBLISHED);
            } else if (action.equalsIgnoreCase("APPROVED")) {
                comment.setStatus(Status.PUBLISHED);
            } else if (action.equalsIgnoreCase("DELETED")) {
                comment.setStatus(Status.DELETED);
            }
            commentService.save(comment);

            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
