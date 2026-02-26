package com.example.everything;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        //Default fragment
        loadFragment(new HomeFragment());
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id=item.getItemId();
            if(id==R.id.nav_home){
                selected=new HomeFragment();
            }else if(id==R.id.nav_explore){
                selected=new ExploreFragment();
            } else if (id==R.id.nav_events) {
                selected=new MyEventsFragment();
            }else {
                selected=new ProfileFragment();
            }
            loadFragment(selected);
            return true;
        });
    }
    private void loadFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
    }
}