package com.example.everything;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private Context context;
    private List<Member> memberList;
    private String currentUserRole;
    private String currentUserId;
    public interface OnMemberClickListener{
        void onManageClick(Member member,int position);
    }
    private OnMemberClickListener listener;
    public MemberAdapter(Context context, List<Member>memberList,String currentUserRole,OnMemberClickListener listener){
        this.context=context;
        this.memberList=memberList;
        this.currentUserRole=currentUserRole;
        this.listener=listener;
        
        // Get current user ID from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("EverythingSession", Context.MODE_PRIVATE);
        currentUserId = String.valueOf(prefs.getInt("userId", 0));
    }
    
    public void updateCurrentUserRole(String role) {
        this.currentUserRole = role;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_member,parent,false);
        return new MemberViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder,int position){
        Member member=memberList.get(position);
        holder.tvName.setText(member.getName());
        holder.tvJoinDate.setText("Joined " + member.getJoinDate());
        String role = member.getRole();
        //role badge visible only to adminn and mod
        if (role.equals("moderator") || role.equals("admin")) {
            holder.tvRoleBadge.setVisibility(View.VISIBLE);
            holder.tvRoleBadge.setText(role.equals("admin") ? "ADMIN" : "MOD");
            holder.tvRoleBadge.setBackgroundColor(
                    role.equals("admin")
                            ? Color.parseColor("#FF6B35")
                            : Color.parseColor("#7B61FF"));
        } else {
            holder.tvRoleBadge.setVisibility(View.GONE);
        }
        // Only admins can manage member roles, but not themselves or other admins
        boolean isCurrentUser = currentUserId.equals(member.getId());
        boolean canManage = currentUserRole.equals("admin") && 
                         !role.equals("admin") && 
                         !isCurrentUser;

        if (canManage) {
            holder.btnManage.setVisibility(View.VISIBLE);
            holder.btnManage.setOnClickListener(v ->
                    listener.onManageClick(member, holder.getAdapterPosition()));
        } else {
            holder.btnManage.setVisibility(View.GONE);
        }
    }
    public void updateItem(int position){
        notifyItemChanged(position);
    }
    @Override
    public int getItemCount(){
        return memberList.size();
    }
    public static class MemberViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvRoleBadge, tvJoinDate;
        ImageView ivAvatar;
        Button btnManage;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvRoleBadge = itemView.findViewById(R.id.tvMemberRoleBadge);
            tvJoinDate = itemView.findViewById(R.id.tvJoinDate);
            ivAvatar = itemView.findViewById(R.id.ivMemberAvatar);
            btnManage = itemView.findViewById(R.id.btnManage);
        }
    }
}