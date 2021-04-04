package com.prodev.bloggingservice.blog.post;

import com.prodev.bloggingservice.auth.AuthUser;
import com.prodev.bloggingservice.model.enums.Status;
import com.prodev.bloggingservice.service.CommonService;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService extends CommonService<Post> {

    Page<Post> findAllByStatus(Status status, int page, int size) throws Exception;

    Page<Post> findAllByTagName(int page, int size, String tag) throws Exception;

    Page<Post> findAllByCategoryName(int page, int size, String category) throws Exception;

    List<PostCategorySummaryDTO> findPostSummaryByCategory() throws Exception;

    Post save(PostDTO postDto, AuthUser authenticatedUser) throws Exception;

    Post findByTitle(String title) throws Exception;

    BlogDTOBuilder getPostDetails(String title, AuthUser auth) throws Exception;

    void postLike(AuthUser user, long postId) throws Exception;

}
