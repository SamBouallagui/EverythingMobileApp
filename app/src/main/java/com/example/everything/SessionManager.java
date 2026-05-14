package com.example.everything;

import android.content.SharedPreferences;
import android.content.Context;

// Gère les données de session utilisateur en utilisant SharedPreferences
// Gère l'état de connexion et la persistance des infos utilisateur
public class SessionManager {
    // Nom du fichier SharedPreferences pour stocker les données de session
    private static final String PREF_NAME = "EverythingSession";
    
    // Clés pour stocker différentes données utilisateur
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    // Initialiser SharedPreferences et editor
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    
    // Sauvegarder la session utilisateur après connexion réussie
    public void saveSession(String token, int userId,
                            String username, String email, String role) {
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply(); //
    }
    
    // Méthodes getter
    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public int getUserId() { return prefs.getInt(KEY_USER_ID, -1); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, null); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }

    // Vérifier si l'utilisateur est actuellement connecté
    public boolean isLoggedIn() { return getToken() != null; }

    // Effacer toutes les données de session quand l'utilisateur se déconnecte
    public void logout() { editor.clear().apply(); }
}
