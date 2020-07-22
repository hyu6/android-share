package edu.neu.madcourse.share.Model;

public class Post {
    private String postID;
    private String postIMG;
    private String postContent;
    private String title;
    private String author;

    public Post(){

    }

    public Post(String postID, String postIMG, String postContent, String title, String author) {
        this.postID = postID;
        this.postIMG = postIMG;
        this.postContent = postContent;
        this.title = title;
        this.author = author;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostIMG() {
        return postIMG;
    }

    public void setPostIMG(String postIMG) {
        this.postIMG = postIMG;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
