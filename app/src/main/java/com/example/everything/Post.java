package com.example.everything;
import com.example.everything.models.api.PostDto;
import java.io.Serializable;

// Classe modèle pour représenter un post dans l'app
// Convertit la réponse API en format prêt à afficher
public class Post implements Serializable{
    private String id;
    private String authorId;
    private String authorName;
    private String authorRole;
    private String content;
    private String timeAgo;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    
    // Constructeur pour créer des posts
    public Post(String id,String authorName,String authorRole,String content,String timeAgo,int likeCount,int commentCount,boolean isLiked){
        this.id=id;
        this.authorId="0";
        this.authorName=authorName;
        this.authorRole=authorRole;
        this.content=content;
        this.timeAgo=timeAgo;
        this.likeCount=likeCount;
        this.commentCount=commentCount;
        this.isLiked=isLiked;
    }
    
    public Post(PostDto dto) {
        this.id = String.valueOf(dto.getId());
        this.authorId = dto.getAuthorId() > 0 ? String.valueOf(dto.getAuthorId()) : "0";
        this.authorName = dto.getAuthorUsername() != null ? dto.getAuthorUsername() : "Unknown User";
        this.authorRole = "Member";
        this.content = dto.getContent();
        this.timeAgo = formatTimeAgo(dto.getCreatedAt());
        this.likeCount = dto.getLikeCount();
        this.commentCount = dto.getCommentCount();
        this.isLiked = dto.isLiked();
    }
    
    // Méthode helper pour formater l'heure de création en format lisible
    private String formatTimeAgo(String createdAt) {
        if (createdAt == null) return "Unknown time";
        
        try {
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date createdDate = isoFormat.parse(createdAt);
            
            if (createdDate != null) {
                java.util.Date now = new java.util.Date();
                long diffInMillis = now.getTime() - createdDate.getTime();
                long diffInSeconds = diffInMillis / 1000;
                long diffInMinutes = diffInSeconds / 60;
                long diffInHours = diffInMinutes / 60;
                long diffInDays = diffInHours / 24;
                
                if (diffInSeconds < 60) {
                    return "Il y a quelques instants";
                } else if (diffInMinutes < 60) {
                    return diffInMinutes + " minute" + (diffInMinutes == 1 ? "" : "s") + " avant";
                } else if (diffInHours < 24) {
                    return diffInHours + " heure" + (diffInHours == 1 ? "" : "s") + " avant";
                } else if (diffInDays < 7) {
                    return diffInDays + " jour" + (diffInDays == 1 ? "" : "s") + " avant";
                } else {
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault());
                    return dateFormat.format(createdDate);
                }
            }
        } catch (Exception e) {
        }
        
        return "Récemment";
    }
    
    // Méthodes getter
    public String getId(){return id;}
    public String getAuthorId(){return authorId;}
    public String getAuthorName(){return authorName;}
    public String getAuthorRole(){return authorRole;}
    public String getContent(){return content;}
    public String getTimeAgo(){return timeAgo;}
    public int getLikeCount(){return likeCount;}
    public int getCommentCount(){return commentCount;}
    public boolean isLiked(){return isLiked;}
    
    // Méthodes setter
    public void setLiked(boolean liked){isLiked=liked;}
    public void setLikeCount(int likeCount){this.likeCount=likeCount;}
    public void setCommentCount(int commentCount){this.commentCount=commentCount;}
}
