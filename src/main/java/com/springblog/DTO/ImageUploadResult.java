package com.springblog.DTO;

public class ImageUploadResult {
    private String bannerImage;
    private String inlineImage;

    public String getBannerImage() {
        return bannerImage;
    }
    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getInlineImage() {
        return inlineImage;
    }
    public void setInlineImage(String inlineImage) {
        this.inlineImage = inlineImage;
    }
}
