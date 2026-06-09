package com.example.expenceiq.ui;

import android.widget.Spinner;
import java.util.List;
import android.widget.ArrayAdapter;
import com.example.expenceiq.data.model.Company;

// Standard Android imports
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

// AndroidX imports
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Project imports
import com.example.expenceiq.R;
import com.example.expenceiq.data.api.AdminApiService;
import com.example.expenceiq.utils.SessionManager;

// Java and Retrofit imports
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Activity for Admin to create new users.
 * Extends AppCompatActivity to provide modern UI features and Context compatibility.
 */
public class CreateUserActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword;
    private SessionManager sessionManager;
    private AdminApiService adminApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure activity_create_user.xml exists in res/layout
        setContentView(R.layout.activity_create_user);

        // Initialize SessionManager with Activity context
        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerCompany = findViewById(R.id.spinnerCompany);

        // Initialize Retrofit specifically for the Admin API
        // Using 10.0.2.2 to point to the host machine's localhost from the emulator
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        adminApiService = retrofit.create(AdminApiService.class);
        loadCompanies();

        // Set click listener for the submit button
        findViewById(R.id.btnSubmit).setOnClickListener(v -> performCreateUser());
    }

    private void loadCompanies() {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        adminApiService.getCompanies(token).enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(@NonNull Call<List<Company>> call, @NonNull Response<List<Company>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    companyList = response.body();
                    // Populate spinner with Company objects; Company.toString() returns name
                    ArrayAdapter<Company> adapter = new ArrayAdapter<>(CreateUserActivity.this,
                            android.R.layout.simple_spinner_item, companyList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCompany.setAdapter(adapter);
                } else {
                    Toast.makeText(CreateUserActivity.this, "Failed to load companies", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Company>> call, @NonNull Throwable t) {
                Toast.makeText(CreateUserActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gathers input data and calls the backend API to create a user.
     */
    private Spinner spinnerCompany;
    private List<Company> companyList;

    private void performCreateUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic input validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the request payload
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("role_name", "USER"); // Per requirement: Admin creates regular users
        // Use selected company from spinner
        int selectedCompanyId = ((Company)spinnerCompany.getSelectedItem()).getId();
        userData.put("company_id", selectedCompanyId);

        // Construct Authorization Header
        String savedToken = sessionManager.fetchAuthToken();
        if (savedToken == null) {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }
        String token = "Bearer " + savedToken;

        // Execute asynchronous API call
        adminApiService.createUser(token, userData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateUserActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to dashboard
                } else {
                    String message = "Error: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            message += " - " + response.errorBody().string();
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(CreateUserActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(CreateUserActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
