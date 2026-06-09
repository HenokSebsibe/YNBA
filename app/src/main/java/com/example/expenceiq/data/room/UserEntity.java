// Package for Room entities
package com.example.expenceiq.data.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Minimal Room entity for a User.
 * Stores only the fields required for the current app flow.
 */
@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Username used for login / display
    public String username;

    // Foreign key – the company this user belongs to (optional)
    public Long companyId;

    // Simple constructor
    public UserEntity(String username, Long companyId) {
        this.username = username;
        this.companyId = companyId;
    }
}
