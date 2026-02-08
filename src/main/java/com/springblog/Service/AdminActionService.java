package com.springblog.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.springblog.Entities.Article;
import com.springblog.Entities.User;
import com.springblog.Entities.enums.BlogStatus;
import com.springblog.Exception.AlreadyPublishedException;
import com.springblog.Repository.ArticleRepo;
import com.springblog.Repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminActionService {

	@Autowired
	private ArticleRepo repo;
	@Autowired
	private UserRepo urepo;
	 @Value("${file.upload-dir}")
	 private String uploadDir;
	 
	@Transactional
	public void rejectArticle(int id, String reason) {
	    Article article = repo.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Article not found"));

	    article.setStatus(BlogStatus.REJECTED);
	    article.setRejectionReason(reason); // overwrite
	    repo.save(article);
	}

	
	
	
	public List<String> adminuploadMovieImageService(MultipartFile file1,MultipartFile file2) {
		List<String> ans=new ArrayList<>(2);
		String fileName1 ="";
	    String fileName2 = "";

		try{
			if(file1.isEmpty() && file2.isEmpty())return ans;
			File folder = new File(uploadDir);
			if (!folder.exists()) folder.mkdirs();
			if(!file1.isEmpty()) {
			    UUID uuid1 = UUID.randomUUID();
		        String uuidAsString1 = uuid1.toString();
		        String originalName1 = file1.getOriginalFilename();
		        String extension1 = originalName1.substring(originalName1.lastIndexOf("."));
			    fileName1 = uuidAsString1.substring(0, 30)+extension1;
			    fileName1=fileName1.replaceAll("-", "");
			    System.out.println(fileName1);
			    Path path1 = Paths.get(uploadDir + fileName1);
				Files.copy(file1.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING);
			    System.out.println("uploaded1");

				ans.add(0, fileName1);

			}
			if(!file2.isEmpty()) {
				  UUID uuid2 = UUID.randomUUID();
			        String uuidAsString2 = uuid2.toString();
			        String originalName2 = file2.getOriginalFilename();
			        String extension2 = originalName2.substring(originalName2.lastIndexOf("."));
				    fileName2 = uuidAsString2.substring(0, 30)+extension2;
				    fileName2=fileName2.replaceAll("-", "");
				    System.out.println(fileName2);
				    Path path2 = Paths.get(uploadDir + fileName2);
					Files.copy(file2.getInputStream(), path2, StandardCopyOption.REPLACE_EXISTING);
				    System.out.println("uploaded2");
//				    ans.add(fileName2);
				    ans.add(1, fileName2);
			}
		  
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}


	    public Article getPendingArticle(int id) {
	        Article article = repo.findByIdAndActiveTrue(id)
	                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

	        if (article.getStatus() != BlogStatus.PENDING) {
	            throw new IllegalStateException("Article not pending review");
	        }
	        return article;
	    }
	    
	    @Transactional
	    public void approveArticle(int id) {
	        Article article = getPendingArticle(id);
	        article.setStatus(BlogStatus.APPROVED);
	        repo.save(article);
	    }


		@Transactional
	    public Article publishArticle(int id) {
	        Article article = getApprovedArticle(id);
	        article.setStatus(BlogStatus.PUBLISHED);
	        article.setPublishedAt(LocalDateTime.now());
	        repo.save(article);
	        return article;
	    }


		private Article getApprovedArticle(int id) {
			Article article = repo.findByIdAndActiveTrue(id)
	                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

			
			   if (article.getStatus() == BlogStatus.PUBLISHED) {
		            throw new AlreadyPublishedException("Article already published");
		        }
			   
	        if (article.getStatus() != BlogStatus.APPROVED) {
	            throw new IllegalStateException("Article not APPROVED review");
	        }
	     
			return article;
		}


		public Article getById(int id) {
			// TODO Auto-generated method stub
			Article article = repo.findById(id).get();
			return article;
		}

		
		   @Transactional
		    public void softDelete(Article article) {
		        article.setActive(false);
		        article.setStatus(BlogStatus.ARCHIVED); // recommended
		        repo.save(article);
		    }
		   
//done
			 public Page<Article> getActiveBlogs(int page, int size) {
			        Pageable pageable = PageRequest.of(
			            page,
			            size,
			            Sort.by("createdAt").descending()
			        );
			        return repo.findAll(pageable);
			 }
			 public void adminSaveArticle(Article article) {
				 repo.save(article);
			 }


			 public User getLoggedInUserByUserName(String name) {
				// TODO Auto-generated method stub
					User u=urepo.getUserByUserName(name);
				return u;
			 }
	
}
