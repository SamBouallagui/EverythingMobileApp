package com.example.everything;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import java.util.ArrayList;

public class ExploreFragment extends Fragment {
    private RecyclerView rvCommunities;
    private CommunityAdapter adapter;
    private List<Community> communityList=new ArrayList<>();
    private List<Community> filteredList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@NonNull ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_explore,container,false);
        rvCommunities = view.findViewById(R.id.rvCommunities);
        EditText etSearch=view.findViewById(R.id.etSearch);
        loadFakeCommunities();
        rvCommunities.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new CommunityAdapter(getContext(), filteredList, new CommunityAdapter.OnCommunityClickListener() {
            @Override
            public void onCommunityClick(Community community) {
                Intent intent = new Intent(getActivity(),CommunityDetailActivity.class);
                intent.putExtra("community",community);
                startActivity(intent);
            }

            @Override
            public void onJoinClick(Community community, int position) {
                community.setJoined(!community.isJoined());
                adapter.updateItem(position);
                String msg=community.isJoined()
                        ? "Joined " + community.getName()
                        : "Left " +community.getName();
                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
        rvCommunities.setAdapter(adapter);
        //each time the EditText is changed filter the communities
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            filterCommunities(s.toString());

            }
        });
        return view;
    }
    private void filterCommunities(String query){
        filteredList.clear();
        if(query.isEmpty()){
            filteredList.addAll(communityList);
        }else{
            String lower=query.toLowerCase();
            for(Community c:communityList){
                if(c.getName().toLowerCase().contains(lower)|| c.getCategory().toLowerCase().contains(lower)){
                    filteredList.add(c);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void loadFakeCommunities(){
        communityList.add(new Community("1", "Trail Runners Tunis",
                "Running", "A group for trail running lovers in and around Tunis.",
                128, "", false));
        communityList.add(new Community("2", "Atlas Hikers",
                "Hiking", "Exploring the Atlas mountains one trail at a time.",
                74, "", false));
        communityList.add(new Community("3", "Sahara Cyclists",
                "Cycling", "Road and mountain biking across Tunisia.",
                53, "", false));
        communityList.add(new Community("4", "Bookworms TN",
                "Reading", "Monthly book club with a mix of Arabic and French novels.",
                210, "", false));
        communityList.add(new Community("5", "Photography Collective",
                "Photography", "Share your shots and learn from fellow photographers.",
                95, "", false));
        filteredList.addAll(communityList);
    }
}