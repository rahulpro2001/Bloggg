package com.springblog.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springblog.DTO.ImageUploadResult;
import com.springblog.Entities.Article;
import com.springblog.Entities.User;
import com.springblog.Entities.enums.BlogStatus;
import com.springblog.Repository.ArticleRepo;
import com.springblog.Repository.UserRepo;
import com.springblog.Service.UserPublisherService;
import com.springblog.security.CustomUserDetails;

import jakarta.persistence.EntityNotFoundException;

@Controller
public class UserPublisherController {

	
		@Autowired
    private PasswordEncoder encoder;
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ArticleRepo repo;
	private final UserPublisherService userpublisherService;
	
	UserPublisherController(UserPublisherService userpublisherService){
		this.userpublisherService=userpublisherService;
	}

	@GetMapping("/")
	public String Blogs(
	        @RequestParam(defaultValue = "0") int page,
	        Model model
	) {
//		System.out.println(userpublisherService.getActivePublishedBlogs(page, 5));
//	    model.addAttribute("articles",userpublisherService.getActivePublishedBlogs(page, 5));
		
	    model.addAttribute("featuredBlogs", userpublisherService.getActiveAndHighestViewsBlog());
//	    System.out.println(userpublisherService.getRecentUploadedArticles(BlogStatus.PUBLISHED).size());
	    model.addAttribute("recentBlogs", userpublisherService.getRecentUploadedArticles(BlogStatus.PUBLISHED));
		Page<Article> blogPage = userpublisherService.getActivePublishedBlogs(page, 2);

	    model.addAttribute("blogs", blogPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", blogPage.getTotalPages());
	    model.addAttribute("isFirst", blogPage.isFirst());
	    model.addAttribute("isLast", blogPage.isLast());
//	    model.addAttribute("blogs", userpublisherService.getActivePublishedBlogs(page, 5));
	    return "blogs";
	}
	

	@GetMapping("/reg")
	public String showRegister(Model m) {
		m.addAttribute("user", new User());
		return "register";
	}
	
	@PostMapping("/register")
	public String registerNewUser(@ModelAttribute User user) {
		 	user.setRole("ROLE_USER");
		    user.setEnabled(true);
		    user.setActive(true);
		    user.setCreatedAt(LocalDateTime.now());
		    user.setPassword(encoder.encode(user.getPassword()));
		    System.out.println(user);
		    userpublisherService.saveUser(user);
//		   userRepo.save(user);
		    return "redirect:/";
	}
	@GetMapping("/mylogin")
	public String gotoLoginPage() {
		return "login";
	}
//	@GetMapping("/article/{id}")
//	public String ArticleById(@PathVariable int id, Model m) {
//		Article a = new Article();
//		Optional<Article> temp= repo.findById(id);
//		a=temp.get();
//	    userpublisherService.IncreasePageViews(a);
//		m.addAttribute("article", a);
//	    m.addAttribute("articles", userpublisherService.getRecentUploadedArticles(BlogStatus.PUBLISHED));
//		return "article";
//	}
	

	@GetMapping("/article/{slug}")
	public String ArticleBySlug(@PathVariable String slug, Model m) {
	    // 1. Fetch by slug instead of ID
		  Article a = userpublisherService.findBySlug(slug);
//	    Article a = repo.findBySlug(slug)
//	        .orElseThrow(() -> new EntityNotFoundException("Article not found with slug: " + slug));

	    // 2. Increment views as usual
	    System.out.println(a);
	    userpublisherService.IncreasePageViews(a);
	    
	    m.addAttribute("article", a);
	    m.addAttribute("articles", userpublisherService.getRecentUploadedArticles(BlogStatus.PUBLISHED));
	    
	    return "article";
	}
	
	
	@GetMapping("/user/article/new")
	public String UserOpenAddArticle(Model m,Principal user) {
		m.addAttribute("article", new Article());
		return "user_create_article";
	}
	
	@PostMapping("/user/article/add")
	public String UserAddNewArticle(
	        @ModelAttribute Article article,
	        Principal principal,
	        @RequestParam("bannerImage") MultipartFile bannerImage,
	        @RequestParam("inlineImage") MultipartFile inlineImage) {
			User user = userpublisherService.getUserByUserName(principal.getName());
//	    User user = userRepo.getUserByUserName(principal.getName());

	    article.setAuthor(user);
	    article.setActive(true);
	    article.setStatus(BlogStatus.DRAFT);
	    article.setCreatedAt(LocalDateTime.now());

	    String slug = generateSlug(article.getTitle());
	    article.setSlug(slug);

	    // ✅ UPLOAD IMAGES (ONCE)
	    ImageUploadResult images =
	            userpublisherService.uploadMovieImageService(bannerImage, inlineImage);

	    if (images.getBannerImage() != null) {
	        article.setImageUrl(images.getBannerImage());   // banner
	    }

	    if (images.getInlineImage() != null) {
	        article.setInLineImage(images.getInlineImage()); // inline
	    }
	    userpublisherService.userSaveArticle(article);
//	    repo.save(article);

	    return "redirect:/";
	}

	
	@PostMapping("/user/article/submit/{id}")
	public String submitForReview(@PathVariable int id) {
	    Article article = userpublisherService.findById(id);
//	    		repo.findById(id).get();

//	    Article article = repo.findById(id).get();
//	        .orElseThrow();

	    article.setStatus(BlogStatus.PENDING);
	    userpublisherService.userSaveArticle(article);
//	    repo.save(article);

	    return "redirect:/user/myblogs?submitted=true";
	}
	public String generateSlug(String text) {
	    return text
	            .toLowerCase()
	            .trim()
	            .replaceAll("[^a-z0-9\\s-]", "")  // remove special chars
	            .replaceAll("\\s+", "-")          // spaces → hyphen
	            .replaceAll("-+", "-");           // multiple hyphens
	}
	
	@GetMapping("/user/myblogs")
	public String showMyBlogs(@RequestParam(defaultValue = "0") int page, Principal principal, Model model) {
	    if (principal == null) {
	        throw new IllegalStateException("User must be logged in");
	    }
	    User user= userpublisherService.getUserByUserName(principal.getName());
//	    User user = userRepo.getUserByUserName(principal.getName());
	    
	    // Fetch 5 blogs per page
	    Page<Article> blogPage = userpublisherService.getMyActiveNonArchivedBlogs(user, page, 4);

	    model.addAttribute("blogs", blogPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", blogPage.getTotalPages());
	    model.addAttribute("isFirst", blogPage.isFirst());
	    model.addAttribute("isLast", blogPage.isLast());

	    return "myblog";
	}
	
	@GetMapping("/user/article/edit/{id}")
	public String editArticle(
	        @PathVariable int id,
	        Model model,
	        @AuthenticationPrincipal CustomUserDetails user) {

//	    Article article = repo.findById(id).get();
	    Article article =userpublisherService.getById(id);

	    // 🔐 security check
	    if (!article.getAuthor().getUsername().equals(user.getUsername())) {
	        throw new AccessDeniedException("Not allowed");
	    }
	    userpublisherService.IncreasePageViews(article);

	    model.addAttribute("article", article);
	    return "user_edit_article";
	}
	

	@PostMapping("/user/article/update/{id}")
	public String updateArticle(
	        @PathVariable int id,
	        @ModelAttribute Article updatedArticle,
	        @RequestParam("bannerImage") MultipartFile bannerImage,
	        @RequestParam("inlineImage") MultipartFile inlineImage,
	        @AuthenticationPrincipal CustomUserDetails user) {
	    Article existing =userpublisherService.getById(id);

//	    Article existing = repo.findById(id)
//	            .orElseThrow(() -> new EntityNotFoundException("Article not found"));

	    // 🔐 ownership check
	    if (!existing.getAuthor().getUsername().equals(user.getUsername())) {
	        throw new AccessDeniedException("Not allowed");
	    }

	    if (existing.getStatus() == BlogStatus.PUBLISHED) {
	        throw new IllegalStateException("Cannot edit published article");
	    }

	    // text fields
	    if (updatedArticle.getTitle() != null && !updatedArticle.getTitle().isBlank())
	        existing.setTitle(updatedArticle.getTitle());

	    if (updatedArticle.getSummary() != null && !updatedArticle.getSummary().isBlank())
	        existing.setSummary(updatedArticle.getSummary());

	    if (updatedArticle.getContent() != null && !updatedArticle.getContent().isBlank())
	        existing.setContent(updatedArticle.getContent());

	    // ✅ image handling (independent)
	    ImageUploadResult images =
	            userpublisherService.uploadMovieImageService(bannerImage, inlineImage);

	    if (images.getBannerImage() != null) {
	        existing.setImageUrl(images.getBannerImage());     // banner only
	    }

	    if (images.getInlineImage() != null) {
	        existing.setInLineImage(images.getInlineImage());  // inline only
	    }
	 // Inside updateArticle method
	    if (updatedArticle.getTitle() != null && !updatedArticle.getTitle().isBlank()) {
	        existing.setTitle(updatedArticle.getTitle());
	        // Regenerate slug if title changes
	        existing.setSlug(generateSlug(updatedArticle.getTitle()));
	    }
	    existing.setStatus(BlogStatus.DRAFT);
	    userpublisherService.userSaveArticle(existing);
//	    repo.save(existing);

	    return "redirect:/";
	}
	@PostMapping("/user/article/delete/{id}")
	public String softDeleteArticle(
	        @PathVariable int id,
	        @AuthenticationPrincipal CustomUserDetails user,
	        RedirectAttributes redirectAttributes) {

	    Article article = userpublisherService.getById(id);

	    // 🔐 ownership check
	    if (!article.getAuthor().getUsername().equals(user.getUsername())) {
	        redirectAttributes.addFlashAttribute(
	            "errorMessage",
	            "You are not allowed to delete this article."
	        );
	        return "redirect:/user/myblogs";
	    }

	    // 🚫 business rule
	    if (article.getStatus() == BlogStatus.PUBLISHED ||
	        article.getStatus() == BlogStatus.APPROVED ||
	        article.getStatus() == BlogStatus.PENDING) {

	        redirectAttributes.addFlashAttribute(
	            "errorMessage",
	            "Published, approved, or pending articles cannot be deleted."
	        );
	        return "redirect:/user/myblogs";
	    }

	    // ✅ soft delete
	    userpublisherService.softDelete(article);

	    redirectAttributes.addFlashAttribute(
	        "successMessage",
	        "Article deleted successfully."
	    );

	    return "redirect:/user/myblogs";
	}
	
	@GetMapping("/user/article/preview/{id}")
	public String previewArticle(
	        @PathVariable int id,
	        Model model,
	        @AuthenticationPrincipal CustomUserDetails user) {

		Article article = userpublisherService.findById(id);
//	    Article article = repo.findById(id)
//	            .orElseThrow(() -> new EntityNotFoundException("Article not found"));
	    
	    // 🔐 ownership check
	    if (!article.getAuthor().getUsername().equals(user.getUsername())) {
	        throw new AccessDeniedException("Not allowed");
	    }
	    userpublisherService.IncreasePageViews(article);
	    model.addAttribute("article", article);
	    return "user_article_preview";
	}
}
