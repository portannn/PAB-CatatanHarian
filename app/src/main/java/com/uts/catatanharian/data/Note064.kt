package com.uts.catatanharian.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Entity catatan untuk Bagian #2 Data Layer (NIM 064).
// Berelasi ke User064 dengan foreign key onDelete CASCADE dan index user_id_064.
@Entity(
    tableName = "notes_064",
    foreignKeys = [ForeignKey(
        entity = User064::class,
        parentColumns = ["id"],
        childColumns = ["user_id_064"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["user_id_064"])]
)
data class Note064(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id_064") val userId: Int,
    @ColumnInfo(name = "judul_064") val judul: String,
    @ColumnInfo(name = "isi_064") val isi: String,
    @ColumnInfo(name = "tanggal_064") val tanggal: Long
)
