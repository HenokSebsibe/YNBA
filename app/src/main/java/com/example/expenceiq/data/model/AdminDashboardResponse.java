package com.example.expenceiq.data.model;

import java.util.List;

public class AdminDashboardResponse {
    private boolean success;
    private AdminData data;

    public boolean isSuccess() { return success; }
    public AdminData getData() { return data; }

    public static class AdminData {
        private double total_income;
        private double total_expenses;
        private double balance;
        private Integer user_count;
        private List<Transaction> recent_transactions;

        public double getTotal_income() { return total_income; }
        public double getTotal_expenses() { return total_expenses; }
        public double getBalance() { return balance; }
        public Integer getUser_count() { return user_count; }
        public List<Transaction> getRecent_transactions() { return recent_transactions; }
    }
}
