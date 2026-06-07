package com.example.expenceiq.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expenceiq.R;
import com.example.expenceiq.data.api.ApiClient;
import com.example.expenceiq.data.model.AdminDashboardResponse;
import com.example.expenceiq.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalIncome, tvTotalExpenses, tvUserCount;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ROLE_DEBUG", "AdminDashboardActivity started");
        
        sessionManager = new SessionManager(this);
        
        // CRITICAL: Verify this is an ADMIN user
        String savedRole = sessionManager.getRole();
        Log.d("ROLE_DEBUG", "AdminDashboardActivity - Saved role = '" + savedRole + "'");
        
        if (savedRole == null || !savedRole.trim().equalsIgnoreCase("ADMIN")) {
            Log.d("ROLE_DEBUG", "AdminDashboardActivity - Non-admin user attempted access. Redirecting to LoginActivity.");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_admin_dashboard);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvUserCount = findViewById(R.id.tvUserCount);

        findViewById(R.id.btnViewUsers).setOnClickListener(v -> 
            startActivity(new Intent(this, UsersListActivity.class)));

        findViewById(R.id.btnCreateUser).setOnClickListener(v -> 
            startActivity(new Intent(this, CreateUserActivity.class)));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadDashboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // On resume, verify admin role is still set
        String savedRole = sessionManager.getRole();
        if (savedRole == null || !savedRole.trim().equalsIgnoreCase("ADMIN")) {
            Log.d("ROLE_DEBUG", "AdminDashboardActivity - onResume: Non-admin role detected. Redirecting to LoginActivity.");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void loadDashboardData() {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        
        // We can reuse the summary endpoint as it now returns role-based data
        ApiClient.INSTANCE.getApiService().getDashboardSummary(token).enqueue(new Callback<com.example.expenceiq.data.model.DashboardResponse>() {
            @Override
            public void onResponse(Call<com.example.expenceiq.data.model.DashboardResponse> call, Response<com.example.expenceiq.data.model.DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.expenceiq.data.model.DashboardData data = response.body().getData();
                    tvTotalIncome.setText("$" + String.format("%.2f", data.getTotal_income()));
                    tvTotalExpenses.setText("$" + String.format("%.2f", data.getTotal_expenses()));
                    tvUserCount.setText(String.valueOf(data.getUser_count() != null ? data.getUser_count() : 0));
                }
            }

            @Override
            public void onFailure(Call<com.example.expenceiq.data.model.DashboardResponse> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Error loading stats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
