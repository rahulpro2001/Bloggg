package com.springblog.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springblog.DTO.ImageUploadResult;
import com.springblog.Entities.Article;
import com.springblog.Entities.User;
import com.springblog.Entities.enums.BlogStatus;
import com.springblog.Repository.ArticleRepo;
import com.springblog.Repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service 
public class UserPublisherService {
	
	@Autowired
	private ArticleRepo articleRepo;
	
	@Autowired
	private UserRepo urepo;
	
	 @Value("${file.upload-dir}")
	 private String uploadDir;

	 public Page<Article> getActivePublishedBlogs(int page, int size) {

	        Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("createdAt").descending()
	        );

	        Page<Article> articles =
	            articleRepo.findByActiveTrueAndStatus(
	                BlogStatus.PUBLISHED,
	                pageable
	            );

	        return articles;
	 }
	 
	 public void userSaveArticle(Article article) {
		 articleRepo.save(article);
	 }


	 public void saveUser(User u) {
		 urepo.save(u);
	 }
	
	 private String saveFile(MultipartFile file) throws IOException {
		    UUID uuid = UUID.randomUUID();
		    String originalName = file.getOriginalFilename();
		    String extension = originalName.substring(originalName.lastIndexOf("."));

		    String fileName = uuid.toString().replace("-", "").substring(0, 30) + extension;

		    Path path = Paths.get(uploadDir + fileName);
		    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		    return fileName;
		}

	 
    public Article getById(int id) {
        return articleRepo.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
    }

    @Transactional
    public void softDelete(Article article) {
        article.setActive(false);
        article.setStatus(BlogStatus.ARCHIVED); // recommended
        articleRepo.save(article);
    }

    public List<Article> getMyActiveBlogs(User user) {
        return articleRepo.findByAuthorAndActiveTrue(user);
    }
    
//    public List<Article> getMyActiveNonArchivedBlogs(User user) {
//        return articleRepo.findByAuthorIdAndActiveTrueAndStatusNot(
//                user.getId(),
//                BlogStatus.ARCHIVED
//        );
//    }
    
    public Page<Article> getMyActiveNonArchivedBlogs(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Assuming you have a method in your Repository like findByUserAndStatusNot(...)
        return articleRepo.findByAuthorIdAndActiveTrueAndStatusNot(user.getId(), BlogStatus.ARCHIVED, pageable);
    }
    @Transactional
    public void resubmitArticle(Article article) {

        if (article.getStatus() == BlogStatus.REJECTED) {
            article.setStatus(BlogStatus.PENDING);
            // ❌ do NOT clear rejectionReason
        }

        articleRepo.save(article);
    }
    
    
    public ImageUploadResult uploadMovieImageService(
            MultipartFile bannerImage,
            MultipartFile inlineImage) {

        ImageUploadResult result = new ImageUploadResult();

        try {
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            if (bannerImage != null && !bannerImage.isEmpty()) {
                result.setBannerImage(saveFile(bannerImage));
            }

            if (inlineImage != null && !inlineImage.isEmpty()) {
                result.setInlineImage(saveFile(inlineImage));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


	public Article getActiveAndHighestViewsBlog() {
		// Pass a PageRequest for size 1 to get only the top one
		List<Article> results = articleRepo.findHighestViewedBlog(PageRequest.of(0, 1));
		Article topBlog = results.isEmpty() ? null : results.get(0);;
		return topBlog;
	}


//	public  List<Article> getRecentCreatedBlog() {
//		// TODO Auto-generated method stub
//		List<Article> articles = articleRepo. 
//		return articles;
//	}


	public List<Article> getRecentUploadedArticles(BlogStatus published) {
		// TODO Auto-generated method stub
		List<Article> articles = articleRepo.findTop7ByStatusOrderByPublishedAtDesc(published);
		return articles;
	}


	public void IncreasePageViews(Article a) {
		// TODO Auto-generated method stub
		a.setViews(a.getViews()+1);
		articleRepo.save(a);
		
	}

	public Article findBySlug(String slug) {
		Article a = articleRepo.findBySlug(slug)
		        .orElseThrow(() -> new EntityNotFoundException("Article not found with slug: " + slug));
		return a;

	}

	public User getUserByUserName(String name) {
	    User user = urepo.getUserByUserName(name);
	    return user;
	}

	public Article findById(int id) {
	    Article article = articleRepo.findById(id).get();
	    return article;
	}

}
