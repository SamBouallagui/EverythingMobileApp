package com.example.everything;
import com.example.everything.utils.AnimationUtils;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
        this.postList=new ArrayList<>(postList);
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
    
    public void updateList(List<Post> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return postList.size(); }
            
            @Override
            public int getNewListSize() { return newList.size(); }
            
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return postList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
            }
            
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Post oldP = postList.get(oldItemPosition);
                Post newP = newList.get(newItemPosition);
                return oldP.isLiked() == newP.isLiked() &&
                       oldP.getLikeCount() == newP.getLikeCount() &&
                       oldP.getCommentCount() == newP.getCommentCount();
            }
        });
        this.postList.clear();
        this.postList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
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
        
        // Set author initial for avatar
        String name = post.getAuthorName();
        if (holder.tvAuthorInitial != null && name != null && !name.isEmpty()) {
            holder.tvAuthorInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
        
        // Like / comment counts without emoji
        holder.btnLike.setText(post.getLikeCount() > 0 ? "Like (" + post.getLikeCount() + ")" : "Like");
        holder.btnComment.setText(post.getCommentCount() > 0 ? "Comment (" + post.getCommentCount() + ")" : "Comment");
        
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
            if (role.equals("admin")) {
                holder.tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_admin);
                holder.tvRoleBadge.setTextColor(Color.parseColor("#FB923C"));
            } else {
                holder.tvRoleBadge.setBackgroundResource(R.drawable.bg_badge_mod);
                holder.tvRoleBadge.setTextColor(Color.parseColor("#A78BFA"));
            }
        } else {
            holder.tvRoleBadge.setVisibility(View.GONE);
        }
        if (post.isLiked()) {
            holder.btnLike.setTextColor(android.graphics.Color.parseColor("#F87171"));
            if (holder.ivLikeIcon != null) {
                holder.ivLikeIcon.setColorFilter(android.graphics.Color.parseColor("#F87171"));
            }
        } else {
            holder.btnLike.setTextColor(android.graphics.Color.parseColor("#6E7681"));
            if (holder.ivLikeIcon != null) {
                holder.ivLikeIcon.setColorFilter(android.graphics.Color.parseColor("#6E7681"));
            }
        }
        
        holder.btnLike.setOnClickListener(v->listener.onLikeClick(post,holder.getAdapterPosition()));
        holder.btnComment.setOnClickListener(v->listener.onCommentClick(post));
        holder.btnDeletePost.setOnClickListener(v->listener.onDeleteClick(post, holder.getAdapterPosition()));
        
        AnimationUtils.addBounceEffect(holder.itemView);
        AnimationUtils.addBounceEffect(holder.btnLike);
        AnimationUtils.addBounceEffect(holder.btnComment);
    }
    public void updateItem(int position){
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount(){
        return postList.size();
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthorName, tvRoleBadge, tvTimeAgo, tvContent, tvAuthorInitial;
        ImageView ivAvatar, ivLikeIcon;
        Button btnLike, btnComment;
        ImageButton btnDeletePost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvRoleBadge = itemView.findViewById(R.id.tvRoleBadge);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvContent = itemView.findViewById(R.id.tvPostContent);
            tvAuthorInitial = itemView.findViewById(R.id.tvAuthorInitial);
            ivLikeIcon = itemView.findViewById(R.id.ivLikeIcon);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
        }
    }
}
