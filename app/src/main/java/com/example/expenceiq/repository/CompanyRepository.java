package com.example.expenceiq.repository;

import android.content.Context;
import com.example.expenceiq.data.api.AdminApiService;
import com.example.expenceiq.data.model.Company;
import com.example.expenceiq.data.room.AppDatabase;
import com.example.expenceiq.data.room.CompanyDao;
import com.example.expenceiq.data.room.CompanyEntity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository that abstracts the data source for Company information.
 * It first tries the local Room cache; if empty it fetches from the remote API
 * and stores the result for future calls.
 */
public class CompanyRepository {
    private final AdminApiService apiService;
    private final CompanyDao companyDao;

    public CompanyRepository(Context context, AdminApiService apiService) {
        this.apiService = apiService;
        AppDatabase db = AppDatabase.getInstance(context);
        this.companyDao = db.companyDao();
    }

    /**
     * Returns a list of Company model objects.
     * If the cache contains data it is returned directly.
     * Otherwise a network request is performed, the response is cached,
     * and the same list is returned.
     */
    public List<Company> getCompanies() {
        List<CompanyEntity> cached = companyDao.getAll();
        if (!cached.isEmpty()) {
            List<Company> result = new ArrayList<>();
            for (CompanyEntity e : cached) {
                // Assuming Company model has a matching constructor
                result.add(new Company((int) e.id, e.name));
            }
            return result;
        }
        // Cache miss – fetch from network
        try {
            Call<List<Company>> call = apiService.getCompanies();
            Response<List<Company>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                // Store each company in the Room cache
                List<CompanyEntity> entities = new ArrayList<>();
                for (Company c : response.body()) {
                    entities.add(new CompanyEntity(c.getName(), null)); // description optional
                }
                companyDao.insertAll(entities);
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace(); // simple error handling for demo
        }
        return new ArrayList<>(); // fallback empty list
    }
}
