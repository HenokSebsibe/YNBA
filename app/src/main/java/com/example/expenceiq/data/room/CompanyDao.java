// Package for Room DAO interfaces
package com.example.expenceiq.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * DAO for accessing Company cache.
 * Provides insert and query methods used by the repository.
 */
@Dao
public interface CompanyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CompanyEntity> companies);

    @Query("SELECT * FROM companies")
    List<CompanyEntity> getAll();

    @Query("DELETE FROM companies")
    void clearAll();
}
