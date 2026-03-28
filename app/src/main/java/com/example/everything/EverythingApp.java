package com.example.everything;

import android.app.Application;

public class EverythingApp  extends Application  {
    @Override
    public void onCreate(){
        super.onCreate();
        // Restaurer le token d'auth si utilisateur était déjà connecté
        ApiClient.initFromSession(this);
    }
}
