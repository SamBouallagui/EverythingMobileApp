    package com.example.everything;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.everything.models.api.PostDto;
import com.example.everything.models.api.MemberDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Fragment for displaying posts within a community
// Shows all posts with like/comment/delete functionality
public class CommunityPostsFragment extends Fragment {
    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private String communityId;
    private String currentUserId;
    private boolean isAdmin = false;
    private boolean isMod = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_posts, container, false);
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get community ID and current user ID from arguments
        if (getArguments() != null) {
            communityId = getArguments().getString("communityId");
        }
        
        // Get current user info from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("EverythingSession", getContext().MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
        
        
        // Load posts and check user permissions
        loadPostsFromApi();
        checkUserRole();
        
        adapter = new PostAdapter(getContext(), postList, new PostAdapter.OnPostClickListener() {
            @Override
            public void onLikeClick(Post post, int position) {
                togglePostLike(post, position);
            }
            
            @Override
            public void onCommentClick(Post post) {
                CommentsDialog commentsDialog = new CommentsDialog(
                    requireContext(),
                    post.getId(), 
                    currentUserId, 
                    isAdmin ? "admin" : (isMod ? "moderator" : "member")
                );
                commentsDialog.show();
            }
            
            @Override
            public void onDeleteClick(Post post, int position) {
                deletePost(post, position);
            }

        });
        rvPosts.setAdapter(adapter);
        
        return view;
    }
    
    private void loadPostsFromApi() {
        if (communityId == null || communityId.equals("0")) {
            Toast.makeText(getContext(), "Invalid community ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        
        
        apiService.getCommunityPosts(communityIdInt).enqueue(new Callback<List<PostDto>>() {
            @Override
            public void onResponse(Call<List<PostDto>> call, Response<List<PostDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PostDto> posts = response.body();
                    
                    postList.clear();
                    for (PostDto dto : posts) {
                        Post post = new Post(dto);
                        postList.add(post);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<PostDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    
    private void deletePost(Post post, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ApiService apiService = ApiClient.getClient().create(ApiService.class);
                    int postId = Integer.parseInt(post.getId());
                    
                    apiService.deletePost(postId).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                postList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    public void refreshPosts() {
        loadPostsFromApi();
    }
    
    private void togglePostLike(Post post, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int postId = Integer.parseInt(post.getId());
        
        
        apiService.togglePostLike(postId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    boolean wasLiked = post.isLiked();
                    post.setLiked(!wasLiked);
                    post.setLikeCount(wasLiked ? post.getLikeCount() - 1 : post.getLikeCount() + 1);
                    adapter.updateItem(position);
                    
                    String message = wasLiked ? "Unliked" : "Liked";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Please login to like posts", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to toggle like", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkUserRole() {
        if (communityId == null || communityId.equals("0")) {
                return;
        }
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        
        apiService.getCommunityMembers(communityIdInt).enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MemberDto> members = response.body();
                    
                    // Find current user and check their role
                    for (MemberDto member : members) {
                        if (member.getUserId() == Integer.parseInt(currentUserId)) {
                            String role = member.getRole();
                            isAdmin = "Admin".equals(role);
                            isMod = "Moderator".equals(role);
                            
                                                
                            // Update PostAdapter with user role
                            if (adapter != null) {
                                String userRole = isAdmin ? "admin" : (isMod ? "moderator" : "member");
                                adapter.updateCurrentUserRole(userRole);
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
            }
        });
    }
    

}