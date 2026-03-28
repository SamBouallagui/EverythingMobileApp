package com.example.everything;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private String currentUserId;
    private String currentUserRole = "member";
    public interface OnPostClickListener{
        void onLikeClick(Post post, int position);
        void onCommentClick(Post post);
        void onDeleteClick(Post post, int position);
    }
    private OnPostClickListener listener;

    public PostAdapter(Context context, List<Post> postList, OnPostClickListener listener){
        this.context=context;
        this.postList=postList;
        this.listener=listener;

        // Get current user ID
        SharedPreferences prefs = context.getSharedPreferences("EverythingSession", Context.MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
    }

    // Method to update current user role
    public void updateCurrentUserRole(String role) {
        this.currentUserRole = role;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        return new PostViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder,int position) {
        Post post = postList.get(position);
        holder.tvAuthorName.setText(post.getAuthorName());
        holder.tvContent.setText(post.getContent());
        holder.tvTimeAgo.setText(post.getTimeAgo());
        holder.btnLike.setText("♥ " + post.getLikeCount());
        holder.btnComment.setText("💬 " + post.getCommentCount());
        
        // Show delete button for post author OR for moderators/admins deleting regular users' posts
        boolean isAuthor = currentUserId.equals(post.getAuthorId());
        boolean canDeleteAsMod = ("moderator".equals(currentUserRole) || "admin".equals(currentUserRole)) 
                              && "member".equals(post.getAuthorRole()) 
                              && !isAuthor;
        
        holder.btnDeletePost.setVisibility(isAuthor || canDeleteAsMod ? View.VISIBLE : View.GONE);
        
        String role = post.getAuthorRole();
        if (role.equals("moderator") || role.equals("admin")) {
            holder.tvRoleBadge.setVisibility(View.VISIBLE);
            holder.tvRoleBadge.setText(role.equals("admin") ? "ADMIN" : "MOD");
            holder.tvRoleBadge.setBackgroundColor(role.equals("admin") ? Color.parseColor("#FF6B35") : Color.parseColor("#7B61FF"));
        } else {
            holder.tvRoleBadge.setVisibility(View.GONE);
        }
        if (post.isLiked()){
            holder.btnLike.setTextColor(Color.parseColor("#FF6B6B"));
        }else{holder.btnLike.setTextColor(Color.parseColor("#AAAAAA"));
        }
        
        holder.btnLike.setOnClickListener(v->listener.onLikeClick(post,holder.getAdapterPosition()));
        holder.btnComment.setOnClickListener(v->listener.onCommentClick(post));
        holder.btnDeletePost.setOnClickListener(v->listener.onDeleteClick(post, holder.getAdapterPosition()));
    }
    public void updateItem(int position){
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount(){
        return postList.size();
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvRoleBadge, tvTimeAgo, tvContent;
        ImageView ivAvatar;
        Button btnLike, btnComment;
        ImageButton btnDeletePost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvRoleBadge = itemView.findViewById(R.id.tvRoleBadge);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvContent = itemView.findViewById(R.id.tvPostContent);
            ivAvatar = itemView.findViewById(R.id.ivAuthorAvatar);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
        }
    }
}
