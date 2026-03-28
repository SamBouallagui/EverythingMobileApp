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
import com.example.everything.models.api.CommunityDto;
import com.example.everything.models.api.MemberDto;
import java.util.List;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        
        loadCommunitiesFromApi();
        
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
                if (community.isJoined()) {
                    leaveCommunity(community, position);
                } else {
                    joinCommunity(community, position);
                }
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
    
    private void loadCommunitiesFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllCommunities().enqueue(new Callback<List<CommunityDto>>() {
            @Override
            public void onResponse(Call<List<CommunityDto>> call, Response<List<CommunityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CommunityDto> communities = response.body();
                    
                    communityList.clear();
                    for (CommunityDto dto : communities) {
                        Community community = new Community(dto);
                        communityList.add(community);
                    }
                    
                    // After loading all communities, check which ones are joined
                    checkJoinedStatus();
                } else {
                    Toast.makeText(getContext(), "Failed to load communities", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<CommunityDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    
    private void checkJoinedStatus() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getJoinedCommunities().enqueue(new Callback<List<CommunityDto>>() {
            @Override
            public void onResponse(Call<List<CommunityDto>> call, Response<List<CommunityDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CommunityDto> joinedCommunities = response.body();
                    
                    java.util.Set<Integer> joinedIds = new java.util.HashSet<>();
                    for (CommunityDto dto : joinedCommunities) {
                        joinedIds.add(dto.getId());
                    }
                    
                    for (int i = 0; i < communityList.size(); i++) {
                        Community community = communityList.get(i);
                        int communityId = Integer.parseInt(community.getId());
                        
                        if (joinedIds.contains(communityId)) {
                            community.setJoined(true);
                        } else {
                            community.setJoined(false);
                        }
                        
                        getCommunityMemberCount(community, i);
                    }
                    
                    filteredList.clear();
                    filteredList.addAll(communityList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CommunityDto>> call, Throwable t) {
                for (Community community : communityList) {
                    community.setJoined(false);
                    community.setMemberCount(0);
                }
                
                filteredList.clear();
                filteredList.addAll(communityList);
                adapter.notifyDataSetChanged();
            }
        });
    }
    
    private void getCommunityMemberCount(Community community, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.getCommunityMembers(communityId).enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int memberCount = response.body().size();
                    community.setMemberCount(memberCount);
                    adapter.updateItem(position);
                } else {
                    community.setMemberCount(0);
                    adapter.updateItem(position);
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                community.setMemberCount(0);
                adapter.updateItem(position);
            }
        });
    }
    
    private void joinCommunity(Community community, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.joinCommunity(communityId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    community.setJoined(true);
                    // Increase member count when joining
                    int currentCount = community.getMemberCount();
                    community.setMemberCount(currentCount + 1);
                    adapter.updateItem(position);
                    Toast.makeText(getContext(), "Joined " + community.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Please login to join communities", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to join community", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void leaveCommunity(Community community, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityId = Integer.parseInt(community.getId());
        
        apiService.leaveCommunity(communityId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    community.setJoined(false);
                    // Decrease member count when leaving
                    int currentCount = community.getMemberCount();
                    community.setMemberCount(Math.max(1, currentCount - 1));
                    adapter.updateItem(position);
                    Toast.makeText(getContext(), "Left " + community.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Please login to leave communities", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to leave community", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void filterCommunities(String query){
        filteredList.clear();
        if(query.isEmpty()){
            filteredList.addAll(communityList);
        }else{
            String lower=query.toLowerCase();
            for(Community c:communityList){
                String category = c.getCategory();
                if((c.getName() != null && c.getName().toLowerCase().contains(lower)) || 
                   (category != null && category.toLowerCase().contains(lower))){
                    filteredList.add(c);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}