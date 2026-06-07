package com.example.expenceiq.data.model;

import java.util.List;

public class UserListResponse {
    private boolean success;
    private List<UserData> data;

    public boolean isSuccess() { return success; }
    public List<UserData> getData() { return data; }

    public static class UserData {
        private int id;
        private String username;
        private String email;
        private String role_name;
        private String status;

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole_name() { return role_name; }
        public String getStatus() { return status; }
    }
}
