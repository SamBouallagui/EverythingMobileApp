package com.example.everything;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.everything.models.api.AuthResponse;
import com.example.everything.models.api.LoginRequest;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Fragment for user login functionality
// Handles email/password authentication and session management
public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);

        // Handle login button click
        view.findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // check if fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(),
                        "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            Context context = getContext();
            if (context == null) return;

            view.findViewById(R.id.btnLogin).setEnabled(false);

            // Make API call to login endpoint
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            LoginRequest request = new LoginRequest(email, password);

            apiService.login(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call,
                                       Response<AuthResponse> response) {
                    view.findViewById(R.id.btnLogin).setEnabled(true);

                    // Check if login was successful
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse auth = response.body();

                        // save token and user info locally
                        new SessionManager(context).saveSession(
                                auth.getToken(),
                                auth.getUser().getId(),
                                auth.getUser().getUsername(),
                                auth.getUser().getEmail(),
                                auth.getUser().getRole()
                        );
                        ApiClient.setToken(auth.getToken());


                        // go to main screen and clear stack
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(context,
                                "Invalid email or password",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    view.findViewById(R.id.btnLogin).setEnabled(true);
                    Toast.makeText(context,
                            "Connection failed: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}