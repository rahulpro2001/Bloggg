package com.springblog.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springblog.Entities.Article;
import com.springblog.Entities.User;
import com.springblog.Entities.enums.BlogStatus;
import com.springblog.Exception.AlreadyPublishedException;
import com.springblog.Service.AdminActionService;
import com.springblog.security.CustomUserDetails;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminArticeController {
	
	@Autowired
    private AdminActionService articleService;
	
	@GetMapping("/adminhome")
	public String PostLoginSuccess(@RequestParam(defaultValue = "0") int page,Model m) {
		Page<Article> articlePage = (Page<Article>) articleService.getActiveBlogs(page, 5);
	    
	    m.addAttribute("articles", articlePage.getContent()); // The 5 records
	    m.addAttribute("currentPage", page);
	    m.addAttribute("totalPages", articlePage.getTotalPages());
	    m.addAttribute("isFirst", articlePage.isFirst());
	    m.addAttribute("isLast", articlePage.isLast());
	    return "admin_dashboard";
	}
	
	@GetMapping("/admin/article/edit/{authorId}")
	public String GoToEditArticlePage(@PathVariable int authorId,Model m) {
		Article Editarticle = articleService.getById(authorId);
		m.addAttribute("article", Editarticle);
		return "admin_edit_article";
	}
	
	@PostMapping("/admin/article/update")
	public String UpdateArticleUsingForm(@ModelAttribute("article") Article article,Model m) {
		Article updateArticle  = new Article();
		updateArticle.setTitle(article.getTitle());
		updateArticle.setContent(article.getContent());
		updateArticle.setUpdatedAt(LocalDateTime.now());
		updateArticle.setCreatedAt(article.getCreatedAt());
		updateArticle.setId(article.getId());
		updateArticle.setSlug(article.getSlug());
		articleService.adminSaveArticle(updateArticle);
        return "redirect:/adminhome";

	}
	
	
	@GetMapping("/admin/article/delete/{id}")
	public String softDeleteArticle(
	        @PathVariable int id,
	        @AuthenticationPrincipal CustomUserDetails user,
	        RedirectAttributes redirectAttributes) {

	    Article article = articleService.getById(id);
	    
	    // 🚫 business rule
	    if (article.getStatus() == BlogStatus.PUBLISHED) {

	        redirectAttributes.addFlashAttribute(
	            "errorMessage",
	            "Published articles cannot be deleted."
	        );
	        return "redirect:/adminhome";
	    }

	    // ✅ soft delete
	    articleService.softDelete(article);

	    redirectAttributes.addFlashAttribute(
	        "successMessage",
	        "Article deleted successfully."
	    );

	    return "redirect:/adminhome";
	}
	
	
	
	@PostMapping("/admin/article/add")
	public String AdminAddNewArticle(@ModelAttribute Article article,Model m,Principal user) {
		System.out.println(article);
		article.setCreatedAt(LocalDateTime.now());
		User u = articleService.getLoggedInUserByUserName(user.getName());
//		User u=urepo.getUserByUserName(user.getName());
		System.out.println(u);
		article.setActive(true);
		article.setAuthor(u);
		articleService.adminSaveArticle(article);
	    return "redirect:/adminhome";

	}

    @GetMapping("/blog/{id}")
    public String reviewArticle(@PathVariable int id, Model model) {
        Article article = articleService.getById(id);
        model.addAttribute("article", article);
        return "admin_review_article";
    }
    
    @PostMapping("admin/article/approve/{id}")
    public String approveArticle(@PathVariable int id) {

        articleService.approveArticle(id);
        return "redirect:/adminhome?approved=true";
    }
    
    
    @GetMapping("/admin/article/publish/{id}")
    public String publishArticle(@PathVariable int id,
                                 RedirectAttributes redirectAttributes) {

        try {
        	Article a = articleService.getById(id);
        	 if (a.getStatus() != BlogStatus.APPROVED) {

 		        redirectAttributes.addFlashAttribute(
 		            "errorMessage",
 		            "Article not approved and cannot be published."
 		        );
 		        return "redirect:/adminhome";
 		    }
        	 
            Article article = articleService.publishArticle(id);
            articleService.adminSaveArticle(article);
//            repo.save(article);

            redirectAttributes.addFlashAttribute("published", true);
            return "redirect:/adminhome";

        } catch (AlreadyPublishedException ex) {
            redirectAttributes.addFlashAttribute("alreadyPublished", true);
            return "redirect:/adminhome";
        }
    }
    
    // REJECT
    @PostMapping("admin/article/reject/{id}")
    public String rejectArticle(@PathVariable int id,
                                @RequestParam String reason) {

        articleService.rejectArticle(id, reason);
        return "redirect:/adminhome?rejected=true";
    }
    
    @PostMapping("/admin/article/update/{id}")
	public String updateArticle(
	        @PathVariable int id,
	        @ModelAttribute Article updatedArticle,
	        @RequestParam("bannerImage") MultipartFile bannerImage,
	        @RequestParam("inlineImage") MultipartFile inlineImage,
	        @AuthenticationPrincipal CustomUserDetails user) {

	    Article existing = articleService.getById(id);

	    if(!updatedArticle.getTitle().isEmpty())
	    existing.setTitle(updatedArticle.getTitle());
	    if(!updatedArticle.getSummary().isEmpty())
	    existing.setSummary(updatedArticle.getSummary());
	    if(!updatedArticle.getContent().isEmpty())
	    existing.setContent(updatedArticle.getContent());

	    // 🖼 update banner only if new image uploaded
	    if (bannerImage != null && !bannerImage.isEmpty()) {
			List<String> images = articleService.adminuploadMovieImageService(bannerImage, inlineImage);
	        System.out.println(images);
			existing.setImageUrl(images.get(0));
	    }
	    // 🖼 update inline image only if provided
	    if (inlineImage != null && !inlineImage.isEmpty()) {
	    	List<String> images = articleService.adminuploadMovieImageService(bannerImage, inlineImage);
	        System.out.println(images);
	    	existing.setImageUrl(images.get(1));
	    }
	    articleService.adminSaveArticle(existing);
//	    repo.save(existing);
	    return "redirect:/adminhome";
	}
	
}
