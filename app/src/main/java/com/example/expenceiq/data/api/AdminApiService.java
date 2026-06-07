package com.example.expenceiq.data.api;

import com.example.expenceiq.data.model.AdminDashboardResponse;
import com.example.expenceiq.data.model.UserListResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminApiService {
    @POST("api/admin/create-user")
    Call<Map<String, Object>> createUser(@Header("Authorization") String token, @Body Map<String, Object> userData);

    @GET("api/admin/users")
    Call<UserListResponse> getUsers(@Header("Authorization") String token);

    @DELETE("api/admin/user/{id}")
    Call<Map<String, Object>> deleteUser(@Header("Authorization") String token, @Path("id") int userId);

    @PUT("api/admin/user/{id}/role")
    Call<Map<String, Object>> updateUserRole(@Header("Authorization") String token, @Path("id") int userId, @Body Map<String, String> roleData);
}
