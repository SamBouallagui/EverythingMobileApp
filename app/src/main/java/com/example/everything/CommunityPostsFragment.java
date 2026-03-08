package com.example.everything;

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

import java.util.ArrayList;
import java.util.List;


public class CommunityPostsFragment extends Fragment {
    private PostAdapter adapter;
    private List<Post> postList=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.fragment_community_posts,container,false);
        RecyclerView rvPosts=view.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        loadFakePosts();
        adapter=new PostAdapter(getContext(), postList, new PostAdapter.OnPostClickListener() {
            @Override
            public void onLikeClick(Post post, int position) {
                if(post.isLiked()){
                    post.setLiked(false);
                    post.setLikeCount(post.getLikeCount()-1);
                }else{
                    post.setLiked(true);
                    post.setLikeCount(post.getLikeCount()+1);
                }
                adapter.updateItem(position);
            }
            @Override
            public void onCommentClick(Post post){
                // TODO: open comments screen
                Toast.makeText(getContext(),
                        "Comments coming soon", Toast.LENGTH_SHORT).show();
            }

        });
        rvPosts.setAdapter(adapter);
        return view;
    }
    private void loadFakePosts() {
        postList.add(new Post("1", "Sami Ben Ali", "admin",
                "Welcome everyone to the community! Feel free to share your runs, ask questions and motivate each other. Let's grow together 🏃",
                "2 hours ago", 14, 3, false));
        postList.add(new Post("2", "Rania Mejri", "moderator",
                "Reminder: our weekly Saturday morning run is still on at 7am. Meet at Parc du Belvédère entrance. All levels welcome!",
                "5 hours ago", 8, 6, true));
        postList.add(new Post("3", "Karim Trabelsi", "member",
                "Just finished my first 10K without stopping. Never thought I'd say that 6 months ago. Thanks to everyone here for the encouragement!",
                "Yesterday", 31, 12, false));
        postList.add(new Post("4", "Yasmine Chaouachi", "member",
                "Does anyone have recommendations for good trail running shoes under 300 TND? Running on rocky terrain mostly.",
                "2 days ago", 5, 9, false));
    }
}