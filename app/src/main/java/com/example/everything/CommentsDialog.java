package com.example.everything;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.everything.models.api.CommentDto;
import com.example.everything.models.api.CreateCommentRequest;
import com.example.everything.models.api.UpdateCommentRequest;

import java.util.ArrayList;
import java.util.List;

public class CommentsDialog extends Dialog {
    private Context context;
    private String postId;
    private String currentUserId;
    private String currentUserRole;
    private RecyclerView rvComments;
    private EditText etNewComment;
    private Button btnSendComment;
    private ProgressBar progressBar;
    private TextView tvNoComments;
    private CommentAdapter adapter;
    private List<Comment> commentList;
    private Comment editingComment;

    public CommentsDialog(@NonNull Context context, String postId, String currentUserId, String currentUserRole) {
        super(context);
        this.context = context;
        this.postId = postId;
        this.currentUserId = currentUserId;
        this.currentUserRole = currentUserRole;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_comments);
        
        setCancelable(false);

        rvComments = findViewById(R.id.rvComments);
        etNewComment = findViewById(R.id.etNewComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        progressBar = findViewById(R.id.progressBar);
        tvNoComments = findViewById(R.id.tvNoComments);

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(context, commentList, currentUserId, currentUserRole, new CommentAdapter.OnCommentClickListener() {
            @Override
            public void onEditClick(Comment comment, int position) {
                editComment(comment);
            }

            @Override
            public void onDeleteClick(Comment comment, int position) {
                deleteComment(comment, position);
            }
        });

        rvComments.setLayoutManager(new LinearLayoutManager(context));
        rvComments.setAdapter(adapter);

        btnSendComment.setOnClickListener(v -> {
            String content = etNewComment.getText().toString().trim();
            if (!content.isEmpty()) {
                if (editingComment != null) {
                    updateComment(content);
                } else {
                    createComment(content);
                }
            }
        });

        // Close button functionality
        ImageView btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            dismiss();
        });
        
        loadComments();
    }

    private void loadComments() {
        showLoading(true);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int postIdInt = Integer.parseInt(postId);
        
        apiService.getPostComments(postIdInt).enqueue(new retrofit2.Callback<List<CommentDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<CommentDto>> call, retrofit2.Response<List<CommentDto>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<CommentDto> commentDtos = response.body();
                    commentList.clear();
                    
                    for (CommentDto dto : commentDtos) {
                        String timeAgo = getTimeAgo(dto.getCreatedAt());
                        
                        String role = dto.getAuthorRole() != null ? dto.getAuthorRole() : "member";
                        
                        commentList.add(new Comment(
                            String.valueOf(dto.getId()),
                            String.valueOf(dto.getPostId()),
                            String.valueOf(dto.getAuthorId()),
                            dto.getAuthorName(),
                            role,
                            dto.getContent(),
                            dto.getCreatedAt(),
                            timeAgo
                        ));
                    }
                    
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                } else {
                    Toast.makeText(context, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<CommentDto>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createComment(String content) {
        showLoading(true);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int postIdInt = Integer.parseInt(postId);
        CreateCommentRequest request = new CreateCommentRequest(content);
        
        apiService.createPostComment(postIdInt, request).enqueue(new retrofit2.Callback<CommentDto>() {
            @Override
            public void onResponse(retrofit2.Call<CommentDto> call, retrofit2.Response<CommentDto> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    CommentDto dto = response.body();
                    String timeAgo = getTimeAgo(dto.getCreatedAt());
                    
                    // Gérer le rôle d'auteur depuis la réponse API
                    String authorRole = dto.getAuthorRole() != null ? dto.getAuthorRole() : "member";
                    
                    Comment newComment = new Comment(
                        String.valueOf(dto.getId()),
                        String.valueOf(dto.getPostId()),
                        String.valueOf(dto.getAuthorId()),
                        dto.getAuthorName(),
                        authorRole,
                        dto.getContent(),
                        dto.getCreatedAt(),
                        timeAgo
                    );
                    
                    commentList.add(newComment);
                    adapter.notifyItemInserted(commentList.size() - 1);
                    etNewComment.setText("");
                    updateEmptyState();
                    
                    Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show();
                    // Garder le dialogue ouvert
                } else {
                    Toast.makeText(context, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CommentDto> call, Throwable t) {
                showLoading(false);
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateComment(String content) {
        showLoading(true);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int postIdInt = Integer.parseInt(postId);
        int commentIdInt = Integer.parseInt(editingComment.getId());
        CreateCommentRequest request = new CreateCommentRequest(content);
        
        apiService.updateComment(postIdInt, commentIdInt, request).enqueue(new retrofit2.Callback<CommentDto>() {
            @Override
            public void onResponse(retrofit2.Call<CommentDto> call, retrofit2.Response<CommentDto> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    CommentDto dto = response.body();
                    String timeAgo = getTimeAgo(dto.getCreatedAt());
                    
                    // Update the comment in the list
                    for (int i = 0; i < commentList.size(); i++) {
                        if (commentList.get(i).getId().equals(editingComment.getId())) {
                            String role = dto.getAuthorRole() != null ? dto.getAuthorRole() : "member";
                            
                            commentList.set(i, new Comment(
                                String.valueOf(dto.getId()),
                                String.valueOf(dto.getPostId()),
                                String.valueOf(dto.getAuthorId()),
                                dto.getAuthorName(),
                                role,
                                dto.getContent(),
                                dto.getCreatedAt(),
                                timeAgo
                            ));
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    
                    // Reset editing state
                    editingComment = null;
                    etNewComment.setText("");
                    btnSendComment.setText("Send Comment");
                    
                    Toast.makeText(context, "Comment updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to update comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CommentDto> call, Throwable t) {
                showLoading(false);
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteComment(Comment comment, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int postIdInt = Integer.parseInt(postId);
        int commentIdInt = Integer.parseInt(comment.getId());
        
        apiService.deleteComment(postIdInt, commentIdInt).enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {
                    commentList.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateEmptyState();
                    
                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editComment(Comment comment) {
        editingComment = comment;
        etNewComment.setText(comment.getContent());
        btnSendComment.setText("Update Comment");
        etNewComment.requestFocus();
    }

    private void showLoading(boolean show) {
        if (getContext() != null) {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                btnSendComment.setEnabled(!show);
                etNewComment.setEnabled(!show);
            });
        }
    }

    private void updateEmptyState() {
        if (getContext() != null) {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                tvNoComments.setVisibility(commentList.isEmpty() ? View.VISIBLE : View.GONE);
                rvComments.setVisibility(commentList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }
    }

    private String getTimeAgo(String createdAt) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(createdAt);
            long now = System.currentTimeMillis();
            long diff = now - date.getTime();
            
            if (diff < 60000) return "just now";
            if (diff < 3600000) return (diff / 60000) + "m ago";
            if (diff < 86400000) return (diff / 3600000) + "h ago";
            if (diff < 604800000) return (diff / 86400000) + "d ago";
            return (diff / 604800000) + "w ago";
        } catch (Exception e) {
            return "recently";
        }
    }
}
