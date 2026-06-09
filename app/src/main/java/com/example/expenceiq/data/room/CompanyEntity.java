// Package for Room entities
package com.example.expenceiq.data.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Simple Room entity representing a Company.
 * Only the fields needed for the UI are stored.
 */
@Entity(tableName = "companies")
public class CompanyEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Company name displayed in the UI
    public String name;

    // Optional description – can be null
    public String description;

    // Constructor for convenience
    public CompanyEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
