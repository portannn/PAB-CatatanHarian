package com.uts.catatanharian.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented test untuk memvalidasi operasi CRUD dan Isolasi Data pada NoteDao064.
 */
@RunWith(AndroidJUnit4::class)
class NoteDao064Test {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao064
    private lateinit var noteDao: NoteDao064

    private var userId1: Int = 0
    private var userId2: Int = 0

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Gunakan in-memory database agar data uji langsung terhapus setelah pengujian selesai
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.userDao064()
        noteDao = db.noteDao064()

        // Siapkan 2 user berbeda untuk pengujian isolasi data
        userId1 = userDao.insertUser064(User064(username = "user_satu_064", password = "password123")).toInt()
        userId2 = userDao.insertUser064(User064(username = "user_dua_064", password = "password456")).toInt()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testCreateAndReadAllByUser() = runBlocking {
        val waktuSekarang = System.currentTimeMillis()

        // 1. CREATE (Insert catatan untuk user 1 dan user 2)
        val noteId1 = noteDao.insertNote064(
            Note064(userId = userId1, judul = "Catatan 1 User 1", isi = "Isi catatan 1", tanggal = waktuSekarang)
        )
        val noteId2 = noteDao.insertNote064(
            Note064(userId = userId1, judul = "Catatan 2 User 1", isi = "Isi catatan 2", tanggal = waktuSekarang + 1000)
        )
        noteDao.insertNote064(
            Note064(userId = userId2, judul = "Catatan User 2", isi = "Ini rahasia user 2", tanggal = waktuSekarang)
        )

        // 2. READ (Verifikasi isolasi data: user 1 hanya mendapat 2 catatan miliknya)
        val listUser1 = noteDao.getAllByUser064(userId1)
        assertEquals(2, listUser1.size)
        // Memastikan urutan tanggal DESC (note 2 lebih baru dari note 1)
        assertEquals(noteId2.toInt(), listUser1[0].id)
        assertEquals(noteId1.toInt(), listUser1[1].id)

        // User 2 hanya melihat 1 catatannya
        val listUser2 = noteDao.getAllByUser064(userId2)
        assertEquals(1, listUser2.size)
        assertEquals("Catatan User 2", listUser2[0].judul)
    }

    @Test
    fun testGetByIdAndDataIsolation() = runBlocking {
        val noteId1 = noteDao.insertNote064(
            Note064(userId = userId1, judul = "Catatan Pribadi", isi = "Isi pribadi", tanggal = System.currentTimeMillis())
        ).toInt()

        // Pemilik asli (userId1) bisa membaca datanya
        val noteUser1 = noteDao.getById064(noteId1, userId1)
        assertNotNull(noteUser1)
        assertEquals("Catatan Pribadi", noteUser1?.judul)

        // User lain (userId2) mencoba membaca data noteId1 -> harus mengembalikan null
        val noteUser2 = noteDao.getById064(noteId1, userId2)
        assertNull(noteUser2)
    }

    @Test
    fun testUpdateAndDataProtection() = runBlocking {
        val noteId = noteDao.insertNote064(
            Note064(userId = userId1, judul = "Judul Awal", isi = "Isi Awal", tanggal = System.currentTimeMillis())
        ).toInt()

        // 1. User lain (userId2) mencoba mengupdate catatan milik user 1 -> harus gagal (0 baris berubah)
        val barisTerubahUserLain = noteDao.updateNote064(noteId, userId2, "Dibajak", "Isi dibajak")
        assertEquals(0, barisTerubahUserLain)

        val catatanBelumBerubah = noteDao.getById064(noteId, userId1)
        assertEquals("Judul Awal", catatanBelumBerubah?.judul)

        // 2. Pemilik asli (userId1) melakukan UPDATE -> harus sukses (1 baris berubah)
        val barisTerubahPemilik = noteDao.updateNote064(noteId, userId1, "Judul Baru", "Isi Baru")
        assertEquals(1, barisTerubahPemilik)

        val catatanSesudahUpdate = noteDao.getById064(noteId, userId1)
        assertEquals("Judul Baru", catatanSesudahUpdate?.judul)
        assertEquals("Isi Baru", catatanSesudahUpdate?.isi)
    }

    @Test
    fun testDeleteAndDataProtection() = runBlocking {
        val noteId = noteDao.insertNote064(
            Note064(userId = userId1, judul = "Akan Dihapus", isi = "Isi", tanggal = System.currentTimeMillis())
        ).toInt()

        // 1. User lain (userId2) mencoba menghapus -> harus gagal (0 baris terhapus)
        val barisTerhapusUserLain = noteDao.deleteNote064(noteId, userId2)
        assertEquals(0, barisTerhapusUserLain)

        // Catatan masih ada
        assertNotNull(noteDao.getById064(noteId, userId1))

        // 2. Pemilik asli (userId1) melakukan DELETE -> harus sukses (1 baris terhapus)
        val barisTerhapusPemilik = noteDao.deleteNote064(noteId, userId1)
        assertEquals(1, barisTerhapusPemilik)

        // Catatan sudah tidak ditemukan
        assertNull(noteDao.getById064(noteId, userId1))
    }

    @Test
    fun testSearchByUser() = runBlocking {
        noteDao.insertNote064(Note064(userId = userId1, judul = "Belajar Android", isi = "Room SQLite", tanggal = 1000))
        noteDao.insertNote064(Note064(userId = userId1, judul = "Kuliah PAB", isi = "Tugas kelompok", tanggal = 2000))
        noteDao.insertNote064(Note064(userId = userId2, judul = "Belajar Android", isi = "Catatan user 2", tanggal = 3000))

        // Cari "Android" untuk user 1 -> hanya mengembalikan 1 catatan milik user 1
        val hasilCari = noteDao.searchByUser064(userId1, "Android")
        assertEquals(1, hasilCari.size)
        assertEquals("Belajar Android", hasilCari[0].judul)
        assertEquals(userId1, hasilCari[0].userId)
    }
}
