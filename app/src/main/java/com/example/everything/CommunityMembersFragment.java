package com.example.everything;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;


public class CommunityMembersFragment extends Fragment {
    private TextView tvMemberCountHeader;

    private MemberAdapter adapter;
    private List<Member> memberList=new ArrayList<>();
    //hardcodedd a changer
    private String currentUserRole="admin";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =inflater.inflate(R.layout.fragment_community_members,container,false);
        RecyclerView rvMembers=view.findViewById(R.id.rvMembers);
        tvMemberCountHeader = view.findViewById(R.id.tvMemberCountHeader);
        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        loadFakeMembers();
        tvMemberCountHeader.setText(memberList.size() + " Members");
        adapter =new MemberAdapter(getContext(), memberList, currentUserRole, new MemberAdapter.OnMemberClickListener() {
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

        // admins can promote to mod or remove
        // mods can only remove regular members
        if (currentUserRole.equals("admin")) {
            if (member.getRole().equals("moderator")) {
                options = new String[]{"Demote to Member", "Remove from Community"};
            } else {
                options = new String[]{"Promote to Moderator", "Remove from Community"};
            }
        } else {
            // mod only gets remove option
            options = new String[]{"Remove from Community"};
        }

        new AlertDialog.Builder(getContext())
                .setTitle(member.getName())
                .setItems(options, (dialog, which) -> {
                    if (currentUserRole.equals("admin")) {
                        if (which == 0) {
                            // promote or demote
                            if (member.getRole().equals("moderator")) {
                                member.setRole("member");
                                Toast.makeText(getContext(),
                                        member.getName() + " demoted to member",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                member.setRole("moderator");
                                Toast.makeText(getContext(),
                                        member.getName() + " promoted to moderator",
                                        Toast.LENGTH_SHORT).show();
                            }
                            adapter.updateItem(position);
                        } else {
                            // remove member
                            memberList.remove(position);
                            adapter.notifyItemRemoved(position);
                            tvMemberCountHeader.setText(memberList.size() + " Members");
                            Toast.makeText(getContext(),
                                    member.getName() + " removed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // moderator removing a member
                        memberList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(),
                                member.getName() + " removed",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadFakeMembers() {
        memberList.add(new Member("1", "Sami Ben Ali",
                "admin", "January 2026", ""));
        memberList.add(new Member("2", "Rania Mejri",
                "moderator", "January 2026", ""));
        memberList.add(new Member("3", "Karim Trabelsi",
                "member", "February 2026", ""));
        memberList.add(new Member("4", "Yasmine Chaouachi",
                "member", "February 2026", ""));
        memberList.add(new Member("5", "Omar Souissi",
                "member", "March 2026", ""));
        memberList.add(new Member("6", "Lina Gharbi",
                "member", "March 2026", ""));
    }
}