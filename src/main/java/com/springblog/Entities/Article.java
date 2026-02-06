package com.springblog.Entities;

import java.time.LocalDateTime;

import com.springblog.Entities.enums.BlogStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Article")
public class Article {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;
	@Column(name="title")
	private String title;
//	(URL-friendly title)
	@Column(nullable = false, unique = true)
	private String slug;
	@Column(length = 4000)
	private String content;
	private String imageUrl;
	private String inLineImage;
	private LocalDateTime  createdAt;
	private LocalDateTime  updatedAt;
	private LocalDateTime  publishedAt;
	@Column(length = 2000)
	private String rejectionReason;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private User author;
	@Column(length = 1000)
	private String summary;
    @Enumerated(EnumType.STRING)
    private BlogStatus status;
    private long views = 0;
	private boolean active;
//	public int getAuthorId() {
//		return authorId;
//	}
//	public void setAuthorId(int authorId) {
//		this.authorId = authorId;
//	}
	
	
	public String getTitle() {
		return title;
	}
	public String getRejectionReason() {
		return rejectionReason;
	}
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
	public String getInLineImage() {
		return inLineImage;
	}
	public void setInLineImage(String inLineImage) {
		this.inLineImage = inLineImage;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}
	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public BlogStatus getStatus() {
		return status;
	}
	public void setStatus(BlogStatus status) {
		this.status = status;
	}
	public long getViews() {
		return views;
	}
	public void setViews(long views) {
		this.views = views;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Article(int authorId, String title, String slug, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.title = title;
		this.slug = slug;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Article() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Article [authorId=" + id + ", title_l=" + title + ", slug=" + slug + ", content=" + content
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	
	
}
