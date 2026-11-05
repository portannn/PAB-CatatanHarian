package com.uts.catatanharian.note

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.catatanharian.auth.LoginActivity
import com.uts.catatanharian.data.AppDatabase
import com.uts.catatanharian.data.Note064
import com.uts.catatanharian.databinding.ActivityNoteList064Binding
import com.uts.catatanharian.util.SessionManager
import com.uts.catatanharian.util.applySystemBarsPadding064
import kotlinx.coroutines.launch

// Layar daftar catatan: RecyclerView, search bar, tambah, edit (klik item), hapus.
class NoteListActivity : AppCompatActivity() {
    private lateinit var binding064: ActivityNoteList064Binding
    private lateinit var session064: SessionManager
    private lateinit var adapter064: NoteAdapter064
    private var animated064 = false

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
        binding064 = ActivityNoteList064Binding.inflate(layoutInflater)
        setContentView(binding064.root)
        binding064.root.applySystemBarsPadding064()

        adapter064 = NoteAdapter064(
            onClick064 = { note064 ->
                startActivity(
                    Intent(this, NoteFormActivity::class.java)
                        .putExtra(NoteFormActivity.EXTRA_NOTE_ID_064, note064.id)
                )
            },
            onDelete064 = { note064 -> confirmDelete064(note064) }
        )
        binding064.rvNotes064.layoutManager = LinearLayoutManager(this)
        binding064.rvNotes064.adapter = adapter064

        binding064.fabAdd064.setOnClickListener {
            startActivity(Intent(this, NoteFormActivity::class.java))
        }
        binding064.btnBack064.setOnClickListener { finish() }
        binding064.etSearch064.doAfterTextChanged { loadNotes064() }
    }

    override fun onResume() {
        super.onResume()
        if (::binding064.isInitialized) loadNotes064()
    }

    // Memuat catatan milik user yang login; kalau ada kata kunci, pakai pencarian.
    private fun loadNotes064() {
        val userId064 = session064.getUserId()
        val keyword064 = binding064.etSearch064.text.toString().trim()
        lifecycleScope.launch {
            val dao064 = AppDatabase.getInstance(this@NoteListActivity).noteDao064()
            val notes064 = if (keyword064.isEmpty()) {
                dao064.getAllByUser064(userId064)
            } else {
                dao064.searchByUser064(userId064, keyword064)
            }
            // Animasi masuk item hanya dijalankan saat daftar pertama kali tampil.
            adapter064.submitList(notes064) {
                if (!animated064 && notes064.isNotEmpty()) {
                    animated064 = true
                    binding064.rvNotes064.scheduleLayoutAnimation()
                }
            }
            binding064.emptyState064.visibility = if (notes064.isEmpty()) View.VISIBLE else View.GONE
            binding064.tvEmpty064.text =
                if (keyword064.isEmpty()) "Belum ada catatan.\nKetuk + untuk menambah."
                else "Tidak ada catatan yang cocok."
        }
    }

    private fun confirmDelete064(note064: Note064) {
        AlertDialog.Builder(this)
            .setTitle("Hapus catatan")
            .setMessage("Hapus \"${note064.judul}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    val dao064 = AppDatabase.getInstance(this@NoteListActivity).noteDao064()
                    dao064.deleteNote064(note064.id, session064.getUserId())
                    Toast.makeText(this@NoteListActivity, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                    loadNotes064()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
