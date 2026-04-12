package com.example.everything;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Activité principale qui gère la navigation entre les fragments
// Point d'entrée après que l'utilisateur se connecte
public class MainActivity extends AppCompatActivity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView bottomNav=findViewById(R.id.bottomNav);
        
        // Commencer avec le fragment home par défaut
        loadFragment(new HomeFragment());
        //Gerer la navigation entre les fragments dans BottomNavigationView
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected;
            int id=item.getItemId();
            
            // Vérifier quel élément de navigation a été cliqué et charger le fragment correspondant
            if(id==R.id.nav_home){
                selected=new HomeFragment();
            }else if(id==R.id.nav_explore){
                selected=new ExploreFragment();
            }else {
                selected=new ProfileFragment();
            }
            loadFragment(selected);
            return true;
        });
    }
    
    // Méthode helper pour charger les fragments dans le conteneur
    private void loadFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
    }
}