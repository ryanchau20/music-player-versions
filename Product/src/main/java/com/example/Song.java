package com.example;

public class Song {
    private String title;
    private String imagePath;
    private String mediaPath;

    public Song(String title, String imagePath, String mediaPath) {
        this.title = title;
        this.imagePath = imagePath;
        this.mediaPath = mediaPath;
    }

    public String getTitle() { 
        return title; 
    }

    public String getImagePath() { 
        return imagePath; 
    }

    public String getMediaPath() { 
        return mediaPath; 
    }

    public void setTitle(String title) { 
        this.title = title;
    }

    public void setImagePath(String image) { 
        this.imagePath = image;
    }

    public void setMediaPath(String media) { 
        this.mediaPath = media; 
    }
}
