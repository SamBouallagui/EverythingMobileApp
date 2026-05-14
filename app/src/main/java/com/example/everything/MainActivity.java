package com.example.everything;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Activité principale qui gère la navigation entre les fragments
// Point d'entrée après que l'utilisateur se connecte
public class MainActivity extends AppCompatActivity{
    
    private HomeFragment homeFragment;
    private ExploreFragment exploreFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        
        // Initialize fragments
        homeFragment = new HomeFragment();
        exploreFragment = new ExploreFragment();
        profileFragment = new ProfileFragment();
        
        activeFragment = homeFragment;
        
        // Setup initial fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, profileFragment, "3").hide(profileFragment)
                .add(R.id.fragmentContainer, exploreFragment, "2").hide(exploreFragment)
                .add(R.id.fragmentContainer, homeFragment, "1")
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int id=item.getItemId();
            
            if(id==R.id.nav_home){
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(homeFragment).commit();
                activeFragment = homeFragment;
            }else if(id==R.id.nav_explore){
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(exploreFragment).commit();
                activeFragment = exploreFragment;
            }else {
                getSupportFragmentManager().beginTransaction().hide(activeFragment).show(profileFragment).commit();
                activeFragment = profileFragment;
            }
            return true;
        });
    }
}