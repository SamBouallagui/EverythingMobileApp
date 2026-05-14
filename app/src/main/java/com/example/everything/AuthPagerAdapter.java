package com.example.everything;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

//adapter to display data in the 2 auth fragments
public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginFragment();
        } else {
            return new RegisterFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}