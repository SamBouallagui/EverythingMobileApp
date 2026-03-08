package com.example.everything;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class CommunityPagerAdapter extends FragmentStateAdapter {
    private String communityId;
    public CommunityPagerAdapter(@NonNull FragmentActivity fragmentActivity,String communityId){
        super(fragmentActivity);
        this.communityId=communityId;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position){
        //pass the community id to each fragment
        //bundle for pass data
        Bundle args=new Bundle();
        args.putString("communityId", communityId);
        Fragment fragment;
        if(position==0){
            fragment=new CommunityPostsFragment();
        }else if (position==1){
            fragment=new CommunityEventsFragment();
        }else{
            fragment=new CommunityMembersFragment();
        }
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int getItemCount(){
        return 3;
    }
}
