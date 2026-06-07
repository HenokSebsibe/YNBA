package com.example.expenceiq.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expenceiq.R;
import com.example.expenceiq.data.api.AdminApiService;
import com.example.expenceiq.data.model.UserListResponse;
import com.example.expenceiq.utils.SessionManager;
import java.util.ArrayList;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersListActivity extends AppCompatActivity implements UserAdapter.OnUserDeleteListener {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private SessionManager sessionManager;
    private AdminApiService adminApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        sessionManager = new SessionManager(this);
        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(new ArrayList<>(), this);
        rvUsers.setAdapter(adapter);

        // Initialize Retrofit for Admin tasks
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        adminApiService = retrofit.create(AdminApiService.class);

        loadUsers();
    }

    private void loadUsers() {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        adminApiService.getUsers(token).enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateUsers(response.body().getData());
                } else {
                    Toast.makeText(UsersListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Toast.makeText(UsersListActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserDelete(int userId) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        adminApiService.deleteUser(token, userId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UsersListActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Refresh list
                } else {
                    Toast.makeText(UsersListActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(UsersListActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
