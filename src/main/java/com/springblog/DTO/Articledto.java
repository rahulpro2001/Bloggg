package com.springblog.DTO;

import java.time.LocalDateTime;

public class Articledto {

	private int authorId;
	private String Title;
//	(URL-friendly title)
	private String slug;
	private String content;
	private LocalDateTime  createdAt;
	private LocalDateTime  updatedAt;
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	@Override
	public String toString() {
		return "Articledto [authorId=" + authorId + ", Title=" + Title + ", slug=" + slug + ", content=" + content
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	public Articledto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Articledto(int authorId, String title, String slug, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		super();
		this.authorId = authorId;
		Title = title;
		this.slug = slug;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
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
	
}
