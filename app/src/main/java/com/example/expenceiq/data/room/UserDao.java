package com.example.expenceiq.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * DAO for accessing User cache.
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :id")
    UserEntity getById(long id);

    @Query("SELECT * FROM users")
    List<UserEntity> getAll();
}
