// Package for Room database definition
package com.example.expenceiq.data.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

/**
 * Central RoomDatabase instance.
 * Declares the entities and provides DAO accessors.
 */
@Database(entities = {CompanyEntity.class, UserEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract CompanyDao companyDao();
    public abstract UserDao userDao();

    /**
     * Get the singleton instance of the database.
     * Uses Application context to avoid memory leaks.
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "expenceiq-db")
                            .fallbackToDestructiveMigration() // simple for now
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
