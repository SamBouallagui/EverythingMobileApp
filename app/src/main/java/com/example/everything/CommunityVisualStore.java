package com.example.everything;

import java.util.ArrayList;
import java.util.List;

public class CommunityVisualStore {
    private static final List<CommunityTheme> THEMES = new ArrayList<>();

    static {
        // Gaming
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_gaming, R.color.niche_gaming_start, R.color.niche_gaming_end));
        // Hiking
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_hiking, R.color.niche_hiking_start, R.color.niche_hiking_end));
        // Coding
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_coding, R.color.niche_coding_start, R.color.niche_coding_end));
        // Music
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_music, R.color.niche_music_start, R.color.niche_music_end));
        // Art
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_art, R.color.niche_art_start, R.color.niche_art_end));
        // Science
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_science, R.color.niche_science_start, R.color.niche_science_end));
        // Food
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_food, R.color.niche_food_start, R.color.niche_food_end));
        // Sports
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_sports, R.color.niche_sports_start, R.color.niche_sports_end));
        // Travel
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_travel, R.color.niche_travel_start, R.color.niche_travel_end));
        // Movie
        THEMES.add(new CommunityTheme(R.drawable.ic_comm_movie, R.color.niche_movie_start, R.color.niche_movie_end));
    }

    public static CommunityTheme getThemeForCommunity(String communityId) {
        if (communityId == null || communityId.isEmpty()) {
            return THEMES.get(0);
        }
        
        try {
            // Use hash code of the ID to get a stable random index
            int index = Math.abs(communityId.hashCode()) % THEMES.size();
            return THEMES.get(index);
        } catch (Exception e) {
            return THEMES.get(0);
        }
    }
}
