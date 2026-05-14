package com.example.everything;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Context;
import com.example.everything.models.api.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        TextInputEditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        view.findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() ||
                    password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(getContext(),
                        "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(getContext(),
                        "Passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }

            Context context = getContext();
            if (context == null) return;

            view.findViewById(R.id.btnRegister).setEnabled(false);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            RegisterRequest request = new RegisterRequest(name, email, password);

            apiService.register(request).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    view.findViewById(R.id.btnRegister).setEnabled(true);

                    if (response.isSuccessful()) {
                        Toast.makeText(context,
                                "Account created! Please log in.",
                                Toast.LENGTH_SHORT).show();

                        // switch to the login tab
                        if (getActivity() != null) {
                            ((AuthActivity) getActivity()).switchToLogin();
                        }
                    } else {
                        Toast.makeText(context,
                                "Registration failed. Email may already be in use.",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    view.findViewById(R.id.btnRegister).setEnabled(true);
                    Toast.makeText(context,
                            "Connection failed: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}