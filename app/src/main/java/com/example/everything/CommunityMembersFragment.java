package com.example.everything;

import android.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.everything.models.api.MemberDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommunityMembersFragment extends Fragment {
    private TextView tvMemberCountHeader;
    private MemberAdapter adapter;
    private List<Member> memberList = new ArrayList<>();
    private String communityId;
    private String currentUserId;
    private boolean isAdmin = false;
    private boolean isMod = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.fragment_community_members,container,false);
        RecyclerView rvMembers=view.findViewById(R.id.rvMembers);
        tvMemberCountHeader = view.findViewById(R.id.tvMemberCountHeader);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Get community ID and current user ID
        if (getArguments() != null) {
            communityId = getArguments().getString("communityId");
        }
        
        SharedPreferences prefs = getContext().getSharedPreferences("EverythingSession", getContext().MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
        
        loadMembersFromApi();
        checkUserRole();
        
        adapter =new MemberAdapter(getContext(), memberList, getCurrentRole(), new MemberAdapter.OnMemberClickListener() {
            @Override
            public void onManageClick(Member member, int position) {
                showManageDialog(member,position);
            }
        });
        rvMembers.setAdapter(adapter);
        
        return view;
    }
    private void showManageDialog(Member member, int position) {
        String[] options;
        String memberRole = member.getRole();

        // admins can promote/demote mods and members
        // mods cannot manage other users (only admins can manage roles)
        if (isAdmin) {
            if (memberRole.equals("admin")) {
                // Can't manage other admins
                Toast.makeText(getContext(), "Cannot manage other admins", Toast.LENGTH_SHORT).show();
                return;
            } else if (memberRole.equals("moderator")) {
                options = new String[]{"Promote to Admin", "Demote to Member"};
            } else {
                options = new String[]{"Promote to Moderator"};
            }
        } else {
            // Mods and regular members cannot manage roles
            Toast.makeText(getContext(), "Only admins can manage member roles", Toast.LENGTH_SHORT).show();
            return;
        }


        new AlertDialog.Builder(getContext())
                .setTitle(member.getName())
                .setItems(options, (dialog, which) -> {
                    
                    if (isAdmin) {
                        if (memberRole.equals("moderator")) {
                            if (which == 0) {
                                // Promote moderator to admin
                                promoteToAdmin(member, position);
                            } else {
                                // Demote moderator to member
                                demoteMember(member, position);
                            }
                        } else {
                            // Promote member to moderator
                            promoteMember(member, position);
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .show();
    }
    
    private String getCurrentRole() {
        if (isAdmin) return "admin";
        if (isMod) return "moderator";
        return "member";
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
                            
                            
                            if (adapter != null) {
                                adapter.updateCurrentUserRole(getCurrentRole());
                            }
                            break;
                        }
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
            }
        });
    }
    
    private void loadMembersFromApi() {
        
        if (communityId == null || communityId.equals("0")) {
            Toast.makeText(getContext(), "Invalid community ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        
        
        apiService.getCommunityMembers(communityIdInt).enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MemberDto> memberDtos = response.body();
                    
                    memberList.clear();
                    
                    for (MemberDto dto : memberDtos) {
                        String joinDate = "Unknown";
                        if (dto.getJoinedAt() != null) {
                            try {
                                java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault());
                                java.util.Date date = isoFormat.parse(dto.getJoinedAt());
                                if (date != null) {
                                    joinDate = dateFormat.format(date);
                                }
                            } catch (Exception e) {
                                joinDate = "Recently";
                            }
                        }
                        
                        String role = dto.getRole();
                        if ("Admin".equals(role)) role = "admin";
                        else if ("Moderator".equals(role)) role = "moderator";
                        else role = "member";
                        
                        memberList.add(new Member(
                            String.valueOf(dto.getUserId()),
                            dto.getUsername(),
                            role,
                            joinDate,
                            ""
                        ));
                    }
                    
                    tvMemberCountHeader.setText(memberList.size() + " Members");
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load members", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void promoteMember(Member member, int position) {
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        int targetUserId = Integer.parseInt(member.getId());
        
        com.example.everything.models.api.UpdateMemberRoleRequest request = 
            new com.example.everything.models.api.UpdateMemberRoleRequest("Moderator");
        
        apiService.updateMemberRole(communityIdInt, targetUserId, request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), member.getName() + " promoted to moderator", Toast.LENGTH_SHORT).show();
                    
                    loadMembersFromApi();
                } else {
                    Toast.makeText(getContext(), "Failed to promote member", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void demoteMember(Member member, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        int targetUserId = Integer.parseInt(member.getId());
        
        com.example.everything.models.api.UpdateMemberRoleRequest request = 
            new com.example.everything.models.api.UpdateMemberRoleRequest("Member");
        
        apiService.updateMemberRole(communityIdInt, targetUserId, request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), member.getName() + " demoted to member", Toast.LENGTH_SHORT).show();
                    
                    loadMembersFromApi();
                } else {
                    Toast.makeText(getContext(), "Failed to demote member", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void promoteToAdmin(Member member, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int communityIdInt = Integer.parseInt(communityId);
        int targetUserId = Integer.parseInt(member.getId());
        
        com.example.everything.models.api.UpdateMemberRoleRequest request = 
            new com.example.everything.models.api.UpdateMemberRoleRequest("Admin");
        
        apiService.updateMemberRole(communityIdInt, targetUserId, request).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), member.getName() + " promoted to admin", Toast.LENGTH_SHORT).show();
                    
                    // Refresh
                    loadMembersFromApi();
                } else {
                    Toast.makeText(getContext(), "Failed to promote member", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}