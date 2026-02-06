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

import jakarta.persistence.EntityNotFoundException;

@Service 
public class UserPublisherService {
	
	@Autowired
	private ArticleRepo articleRepo;
	 public List<Article> getActivePublishedBlogs(int page, int size) {

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

//	        return blogs.stream()
//	                    .map(this::mapToDTO)
//	                    .toList();
//	    }

//	    private BlogResponseDTO mapToDTO(BlogSS blog) {
//	        BlogResponseDTO dto = new BlogResponseDTO();
//	        dto.setTitle(blog.getTitle());
//	        dto.setSlug(blog.getSlug());
//	        dto.setSummary(blog.getSummary());
//	        dto.setPublishedAt(blog.getPublishedAt());
//	        dto.setAuthorName(blog.getAuthor().getUsername());
//	        return dto;
//	    }
	        return articles.getContent();
//	        return (List<Article>) articles;
	 }
	 
	 
	 @Value("${file.upload-dir}")
	 private String uploadDir;
	
//	public List<String> uploadMovieImageService(MultipartFile file1,MultipartFile file2) {
//		List<String> ans=new ArrayList<>(2);
//		String fileName1 ="";
//	    String fileName2 = "";
//
//		try{
//			if(file1.isEmpty() && file2.isEmpty())return ans;
//			File folder = new File(uploadDir);
//			if (!folder.exists()) folder.mkdirs();
//			if(!file1.isEmpty()) {
//			    UUID uuid1 = UUID.randomUUID();
//		        String uuidAsString1 = uuid1.toString();
//		        String originalName1 = file1.getOriginalFilename();
//		        String extension1 = originalName1.substring(originalName1.lastIndexOf("."));
//			    fileName1 = uuidAsString1.substring(0, 30)+extension1;
//			    fileName1=fileName1.replaceAll("-", "");
//			    System.out.println(fileName1);
//			    Path path1 = Paths.get(uploadDir + fileName1);
//				Files.copy(file1.getInputStream(), path1, StandardCopyOption.REPLACE_EXISTING);
//			    System.out.println("uploaded1");
//
//				ans.add(0, fileName1);
//
//			}
//			if(!file2.isEmpty()) {
//				  UUID uuid2 = UUID.randomUUID();
//			        String uuidAsString2 = uuid2.toString();
//			        String originalName2 = file2.getOriginalFilename();
//			        String extension2 = originalName2.substring(originalName2.lastIndexOf("."));
//				    fileName2 = uuidAsString2.substring(0, 30)+extension2;
//				    fileName2=fileName2.replaceAll("-", "");
//				    System.out.println(fileName2);
//				    Path path2 = Paths.get(uploadDir + fileName2);
//					Files.copy(file2.getInputStream(), path2, StandardCopyOption.REPLACE_EXISTING);
//				    System.out.println("uploaded2");
////				    ans.add(fileName2);
//				    ans.add(1, fileName2);
//			}
//		  
////		    return ans;
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		return ans;
//	}
//	
	 
//	 public List<String> uploadMovieImageService(
//		        MultipartFile bannerImage,
//		        MultipartFile inlineImage) {
//
//		    List<String> ans = new ArrayList<>(2);
//
//		    try {
//		        if ((bannerImage == null || bannerImage.isEmpty()) &&
//		            (inlineImage == null || inlineImage.isEmpty())) {
//		            return ans;
//		        }
//
//		        File folder = new File(uploadDir);
//		        if (!folder.exists()) folder.mkdirs();
//
//		        // ✅ Banner image FIRST
//		        if (bannerImage != null && !bannerImage.isEmpty()) {
//		            String bannerFileName = saveFile(bannerImage);
//		            ans.add(bannerFileName); // index 0
//		        }
//
//		        // ✅ Inline image SECOND
//		        if (inlineImage != null && !inlineImage.isEmpty()) {
//		            String inlineFileName = saveFile(inlineImage);
//		            ans.add(inlineFileName); // index 1 (if banner exists)
//		        }
//
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    }
//
//		    return ans;
//		}

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
    
    public List<Article> getMyActiveNonArchivedBlogs(User user) {
        return articleRepo.findByAuthorIdAndActiveTrueAndStatusNot(
                user.getId(),
                BlogStatus.ARCHIVED
        );
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

}
