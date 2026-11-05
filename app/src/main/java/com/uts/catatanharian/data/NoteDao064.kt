package com.uts.catatanharian.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// DAO catatan untuk Bagian #2 Data Layer (NIM 064).
// Semua query difilter berdasarkan user_id_064 untuk menjamin isolasi data antar user.
@Dao
interface NoteDao064 {
    @Insert
    suspend fun insertNote064(note: Note064): Long

    @Query("SELECT * FROM notes_064 WHERE user_id_064 = :userId ORDER BY tanggal_064 DESC")
    suspend fun getAllByUser064(userId: Int): List<Note064>

    @Query("SELECT * FROM notes_064 WHERE id = :id AND user_id_064 = :userId LIMIT 1")
    suspend fun getById064(id: Int, userId: Int): Note064?

    @Query("UPDATE notes_064 SET judul_064 = :judul, isi_064 = :isi WHERE id = :id AND user_id_064 = :userId")
    suspend fun updateNote064(id: Int, userId: Int, judul: String, isi: String): Int

    @Query("DELETE FROM notes_064 WHERE id = :id AND user_id_064 = :userId")
    suspend fun deleteNote064(id: Int, userId: Int): Int

    @Query("SELECT * FROM notes_064 WHERE user_id_064 = :userId AND (judul_064 LIKE '%' || :keyword || '%' OR isi_064 LIKE '%' || :keyword || '%') ORDER BY tanggal_064 DESC")
    suspend fun searchByUser064(userId: Int, keyword: String): List<Note064>
}
