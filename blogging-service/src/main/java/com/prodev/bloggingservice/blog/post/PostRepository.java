package com.prodev.bloggingservice.blog.post;

import com.prodev.bloggingservice.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT new com.prodev.bloggingservice.blog.post.PostCategorySummaryDTO(p.category, COUNT(p))" +
            " FROM Post p where p.status = com.prodev.bloggingservice.model.enums.Status.PUBLISHED" +
            " GROUP BY p.category")
    List<PostCategorySummaryDTO> findCollectionsCategory();

    Page<Post> findAllByStatusOrderByCreatedDateDesc(Status Status, Pageable page);

    Post findByTitle(String title);

    @Query("SELECT p from Post p JOIN p.tags t WHERE p.status = com.prodev.bloggingservice.model.enums.Status.PUBLISHED and t.name = :name")
    Page<Post> findAllByTagName(Pageable page, String name);

    @Query("SELECT p from Post p WHERE p.status = com.prodev.bloggingservice.model.enums.Status.PUBLISHED and (p.category.name = :name OR p.category.name = :name)")
    Page<Post> findAllByCategoryName(Pageable page, String name);

}
