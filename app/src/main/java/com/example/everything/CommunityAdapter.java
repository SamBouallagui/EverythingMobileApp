package com.example.everything;
import com.example.everything.utils.AnimationUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
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
        this.communityList=new ArrayList<>(communityList);
        this.listener=listener;
    }
    
    public void updateList(List<Community> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return communityList.size(); }
            
            @Override
            public int getNewListSize() { return newList.size(); }
            
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return communityList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
            }
            
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Community oldC = communityList.get(oldItemPosition);
                Community newC = newList.get(newItemPosition);
                return oldC.isJoined() == newC.isJoined() && 
                       oldC.getMemberCount() == newC.getMemberCount() &&
                       oldC.getName().equals(newC.getName());
            }
        });
        this.communityList.clear();
        this.communityList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
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
        
        // Apply Premium Theme
        CommunityTheme theme = CommunityVisualStore.getThemeForCommunity(community.getId());
        
        // Icon and Glow
        holder.ivIcon.setImageResource(theme.getIconResId());
        int startColor = ContextCompat.getColor(context, theme.getGradientStart());
        int endColor = ContextCompat.getColor(context, theme.getGradientEnd());
        
        GradientDrawable glowBg = new GradientDrawable();
        glowBg.setShape(GradientDrawable.OVAL);
        glowBg.setColors(new int[]{startColor, 0x00FFFFFF});
        glowBg.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        glowBg.setGradientRadius(120f);
        holder.viewIconGlow.setBackground(glowBg);
        
        // Category Badge with Niche Theme
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setCornerRadius(20f);
        badgeBg.setColor(startColor);
        badgeBg.setAlpha(40); // Subtle background
        holder.tvCategory.setBackground(badgeBg);
        holder.tvCategory.setTextColor(startColor);
        holder.tvCategory.setText(community.getCategory() != null ? community.getCategory() : "Explore");

        holder.tvMemberCount.setText(community.getMemberCount()+" members");
        
        if(community.isJoined()){
            holder.btnJoin.setText("Joined");
            holder.btnJoin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1E1B4B")));
            holder.btnJoin.setTextColor(android.graphics.Color.parseColor("#A78BFA"));
            
            // Show success overlay if it was just joined (simulated here for beauty)
            holder.ivJoinOverlay.setVisibility(View.VISIBLE);
            if (holder.ivJoinOverlay.getDrawable() instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) holder.ivJoinOverlay.getDrawable()).start();
            }
        }else{
            holder.btnJoin.setText("Join");
            holder.btnJoin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(startColor));
            holder.btnJoin.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            holder.ivJoinOverlay.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (community.isJoined()) {
                listener.onCommunityClick(community);
            } else {
                Toast.makeText(context, "Join this community to view details", Toast.LENGTH_SHORT).show();
            }
        });
        holder.btnJoin.setOnClickListener(v -> listener.onJoinClick(community,holder.getAdapterPosition()));
        
        AnimationUtils.addBounceEffect(holder.itemView);
        AnimationUtils.addBounceEffect(holder.btnJoin);
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
        TextView tvName,tvCategory,tvMemberCount;
        Button btnJoin;
        ImageView ivIcon, ivJoinOverlay;
        View viewIconGlow;
        
        public CommunityViewHolder(@NonNull View itemView){
            super(itemView);
            tvName=itemView.findViewById(R.id.tvCommunityName);
            tvCategory=itemView.findViewById(R.id.tvCommunityCategory);
            tvMemberCount=itemView.findViewById(R.id.tvMemberCount);
            btnJoin=itemView.findViewById(R.id.btnJoin);
            ivIcon = itemView.findViewById(R.id.ivCommunityIcon);
            ivJoinOverlay = itemView.findViewById(R.id.ivJoinSuccessOverlay);
            viewIconGlow = itemView.findViewById(R.id.viewIconGlow);
        }
    }
}
