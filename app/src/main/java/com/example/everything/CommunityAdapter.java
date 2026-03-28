package com.example.everything;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private Context context;
    private List<Community> communityList;

    public interface OnCommunityClickListener{
        void onCommunityClick(Community community);
        void onJoinClick(Community community, int position);
    }
    private OnCommunityClickListener listener;
    public CommunityAdapter(Context context,List<Community> communityList, OnCommunityClickListener listener){
        this.context=context;
        this.communityList=communityList;
        this.listener=listener;
    }
    //convert item_community.xml to view object
    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view=LayoutInflater.from(context).inflate(R.layout.item_community,parent,false);
        return new CommunityViewHolder(view);
    }
    //method to set data in each CommunityViewHolder
    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int postion){
        Community community=communityList.get(postion);
        holder.tvName.setText(community.getName());
        String category = community.getCategory();
        holder.tvCategory.setText(category != null ? category : "General");
        holder.tvDescription.setText(community.getDescription());
        holder.tvMemberCount.setText(community.getMemberCount()+" members");
        if(community.isJoined()){
            holder.btnJoin.setText("Joined");
            holder.btnJoin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2A2A4A")));
        }else{
            holder.btnJoin.setText("Join");
            holder.btnJoin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#7B61FF")));

        }
        holder.itemView.setOnClickListener(v -> {
            if (community.isJoined()) {
                listener.onCommunityClick(community);
            } else {
                Toast.makeText(context, "Join this community to view details", Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnJoin.setOnClickListener(v -> listener.onJoinClick(community,holder.getAdapterPosition()));
    }
    public void updateItem(int postion){
        notifyItemChanged(postion);
    }
    @Override
    public int getItemCount(){
        return communityList.size();
    }
    //Memory optimization to not call findViewByID on every scroll
    public static class CommunityViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvCategory,tvDescription,tvMemberCount;
        ImageView ivImage;
        Button btnJoin;
        public CommunityViewHolder(@NonNull View itemView){
            super(itemView);
            tvName=itemView.findViewById(R.id.tvCommunityName);
            tvCategory=itemView.findViewById(R.id.tvCategory);
            tvDescription=itemView.findViewById(R.id.tvDescription);
            tvMemberCount=itemView.findViewById(R.id.tvMemberCount);
            ivImage=itemView.findViewById(R.id.ivCommunityImage);
            btnJoin=itemView.findViewById(R.id.btnJoin);
        }
    }
}
