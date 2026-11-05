package com.uts.catatanharian.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uts.catatanharian.auth.LoginActivity
import com.uts.catatanharian.data.AppDatabase
import com.uts.catatanharian.data.Note064
import com.uts.catatanharian.databinding.ActivityNoteForm064Binding
import com.uts.catatanharian.util.SessionManager
import com.uts.catatanharian.util.applySystemBarsPadding064
import kotlinx.coroutines.launch

// Form tambah catatan. Kalau EXTRA_NOTE_ID_064 diberikan, form berubah jadi mode edit + hapus.
class NoteFormActivity : AppCompatActivity() {
    private lateinit var binding064: ActivityNoteForm064Binding
    private lateinit var session064: SessionManager
    private var noteId064: Int = NO_ID_064

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session064 = SessionManager(this)
        if (!session064.isLoggedIn()) {
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
            return
        }

        enableEdgeToEdge()
        binding064 = ActivityNoteForm064Binding.inflate(layoutInflater)
        setContentView(binding064.root)
        binding064.root.applySystemBarsPadding064()

        noteId064 = intent.getIntExtra(EXTRA_NOTE_ID_064, NO_ID_064)
        val isEdit064 = noteId064 != NO_ID_064
        binding064.tvTitle064.text = if (isEdit064) "Edit Catatan" else "Tambah Catatan"
        binding064.btnDelete064.visibility = if (isEdit064) View.VISIBLE else View.GONE

        binding064.btnBack064.setOnClickListener { finish() }
        binding064.btnSave064.setOnClickListener { save064() }
        binding064.btnDelete064.setOnClickListener { confirmDelete064() }

        if (isEdit064 && savedInstanceState == null) loadNote064()
    }

    // Mengisi form dengan catatan yang diedit. getById064 difilter user, jadi catatan user lain tidak terbuka.
    private fun loadNote064() {
        lifecycleScope.launch {
            val dao064 = AppDatabase.getInstance(this@NoteFormActivity).noteDao064()
            val note064 = dao064.getById064(noteId064, session064.getUserId())
            if (note064 == null) {
                Toast.makeText(this@NoteFormActivity, "Catatan tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            binding064.etJudul064.setText(note064.judul)
            binding064.etIsi064.setText(note064.isi)
        }
    }

    private fun save064() {
        val judul064 = binding064.etJudul064.text.toString().trim()
        val isi064 = binding064.etIsi064.text.toString().trim()

        binding064.tilJudul064.error = if (judul064.isEmpty()) "Judul tidak boleh kosong" else null
        binding064.tilIsi064.error = if (isi064.isEmpty()) "Isi tidak boleh kosong" else null
        if (binding064.tilJudul064.error != null || binding064.tilIsi064.error != null) return

        val userId064 = session064.getUserId()
        lifecycleScope.launch {
            val dao064 = AppDatabase.getInstance(this@NoteFormActivity).noteDao064()
            if (noteId064 == NO_ID_064) {
                dao064.insertNote064(
                    Note064(
                        userId = userId064,
                        judul = judul064,
                        isi = isi064,
                        tanggal = System.currentTimeMillis()
                    )
                )
                Toast.makeText(this@NoteFormActivity, "Catatan disimpan", Toast.LENGTH_SHORT).show()
            } else {
                dao064.updateNote064(noteId064, userId064, judul064, isi064)
                Toast.makeText(this@NoteFormActivity, "Catatan diperbarui", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun confirmDelete064() {
        AlertDialog.Builder(this)
            .setTitle("Hapus catatan")
            .setMessage("Catatan ini akan dihapus permanen. Lanjutkan?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    val dao064 = AppDatabase.getInstance(this@NoteFormActivity).noteDao064()
                    dao064.deleteNote064(noteId064, session064.getUserId())
                    Toast.makeText(this@NoteFormActivity, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    companion object {
        const val EXTRA_NOTE_ID_064 = "note_id_064"
        private const val NO_ID_064 = -1
    }
}
