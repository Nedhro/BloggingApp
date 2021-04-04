package com.prodev.bloggingservice.blog.post;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    PostTag findByNameAndPost(String name, Post post);

    PostTag findByName(String name);

    @Query("SELECT new com.prodev.bloggingservice.blog.post.PostTagSummaryDTO(pt.name, count(pt.post))"
            + " FROM PostTag pt where pt.post.status = com.prodev.bloggingservice.model.enums.Status.PUBLISHED"
            + " GROUP BY pt.name"
            + " ORDER BY count(pt.post) DESC")
    List<PostTagSummaryDTO> getMostUseTags(Pageable page);

    List<PostTag> findAllByPost(Post post);

    @Query("Select pt From PostTag pt where pt.post.status = com.prodev.bloggingservice.model.enums.Status.PUBLISHED and pt.name=:name")
    List<PostTag> findAllByName(String name);

}
