package com.example.everything;
import java.io.Serializable;
public class Post implements Serializable{
    private String id;
    private String authorName;
    private String authorRole;
    private String content;
    private String timeAgo;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    public Post(String id,String authorName,String authorRole,String content,String timeAgo,int likeCount,int commentCount,boolean isLiked){
        this.id=id;
        this.authorName=authorName;
        this.authorRole=authorRole;
        this.content=content;
        this.timeAgo=timeAgo;
        this.likeCount=likeCount;
        this.commentCount=commentCount;
        this.isLiked=isLiked;
    }
    public String getId(){return id;}
    public String getAuthorName(){return authorName;}
    public String getAuthorRole(){return authorRole;}
    public String getContent(){return content;}
    public String getTimeAgo(){return timeAgo;}
    public int getLikeCount(){return likeCount;}
    public int getCommentCount(){return commentCount;}
    public boolean isLiked(){return isLiked;}
    public void setLiked(boolean liked){isLiked=liked;}
    public void setLikeCount(int likeCount){this.likeCount=likeCount;}
    public void setCommentCount(int commentCount){this.commentCount=commentCount;}
}
