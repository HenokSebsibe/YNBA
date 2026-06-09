package com.example.expenceiq.cache;

import android.content.Context;
import com.example.expenceiq.data.api.AdminApiService;
import com.example.expenceiq.data.model.Company;
import com.example.expenceiq.repository.CompanyRepository;
import java.util.List;

/**
 * Simple CacheManager that abstracts away the details of Room caching.
 * It provides a static helper to obtain a list of companies, trying the local
 * cache first and falling back to a network call when necessary.
 *
 * All cache‑related classes (entities, DAO, database) live in the
 * {@code com.example.expenceiq.data.room} package. This wrapper keeps the
 * calling code clean and encourages reuse.
 */
public class CacheManager {
    /**
     * Retrieves a list of companies for the given {@code Context}.
     * The method creates a {@link CompanyRepository} which handles cache ↔
     * network logic internally.
     *
     * @param context Android context (usually an Activity)
     * @param apiService Retrofit service for the Admin API
     * @return List of {@link Company} objects, possibly empty if both cache
     *         and network fail.
     */
    public static List<Company> getCompanies(Context context, AdminApiService apiService) {
        // Repository encapsulates cache‑first strategy
        CompanyRepository repository = new CompanyRepository(context, apiService);
        return repository.getCompanies();
    }
}
