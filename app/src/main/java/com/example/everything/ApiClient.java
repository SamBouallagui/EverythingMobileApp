package com.example.everything;
import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//  gérer la configuration du client API
// Gère l'authentification et la configuration HTTP
public class ApiClient {
    // URL du serveur backend - localhost pour l'émulateur Android
    private static final String BASE_URL = "http://10.0.2.2:5248/";
    private static Retrofit retrofit = null;
    private static String authToken = null;

    // Définir le token d'authentification après connexion utilisateur
    // Réinitialiser retrofit pour appliquer le nouveau token à toutes les requêtes
    public static void setToken(String token) {
        authToken = token;
        // Besoin de recréer l'instance retrofit pour appliquer le nouveau token
        retrofit = null;
    }

    // Obtenir l'instance Retrofit avec la configuration appropriée
    // Crée une nouvelle instance seulement si pas déjà créée
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Ajouter le logging pour voir les requêtes/réponses API dans logcat
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // Configurer OkHttpClient avec les intercepteurs
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    // Intercepteur personnalisé pour ajouter le token JWT à chaque requête
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        // Ajouter l'en-tête Authorization si le token est disponible
                        if (authToken != null) {
                            builder.header("Authorization",
                                    "Bearer " + authToken);
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            // Créer l'instance Retrofit avec URL de base et convertisseurs
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // Supporter les réponses string et JSON
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    // Initialiser le token depuis la session sauvegardée au démarrage de l'app
    // Appelé depuis la classe Application pour assurer que le token est disponible
    public static void initFromSession(Context context) {
        SessionManager session = new SessionManager(context);
        if (session.isLoggedIn()) {
            authToken = session.getToken();
        }
    }
}
