package com.springblog.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.springblog.Entities.Article;
import com.springblog.Entities.User;
import com.springblog.Entities.enums.BlogStatus;

public interface ArticleRepo extends CrudRepository<Article, Integer>{

	List<Article> findByStatusOrderByCreatedAtDesc(BlogStatus published);
	Page<Article> findByActiveTrueAndStatus(
		    BlogStatus status,
		    Pageable pageable
		);
//	List<Article> findAllByUserId(int id);
	List<Article> findAllByAuthorId(int id);
	

    Optional<Article> findByIdAndActiveTrue(int id);

    List<Article> findByAuthorAndActiveTrue(User author);

    List<Article> findByActiveTrueOrderByCreatedAtDesc();

    // admin / trash view (optional)
    List<Article> findByActiveFalse();
    
    List<Article> findByAuthorIdAndActiveTrueAndStatusNot(
            int authorId,
            BlogStatus status
    );

}
