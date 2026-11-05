package com.uts.catatanharian

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.uts.catatanharian.auth.LoginActivity
import com.uts.catatanharian.data.AppDatabase
import com.uts.catatanharian.databinding.ActivityMain064Binding
import com.uts.catatanharian.util.SessionManager
import kotlinx.coroutines.launch

// Dashboard (Bagian #3), NIM 064.
class MainActivity : AppCompatActivity() {
    private lateinit var binding064: ActivityMain064Binding
    private lateinit var session064: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session064 = SessionManager(this)
        // Cek sesi: tanpa login, kembali ke layar Login.
        if (!session064.isLoggedIn()) {
            goToLogin064()
            return
        }

        enableEdgeToEdge()
        binding064 = ActivityMain064Binding.inflate(layoutInflater)
        setContentView(binding064.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding064.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                bars.left + 24.dp064(),
                bars.top + 24.dp064(),
                bars.right + 24.dp064(),
                bars.bottom + 24.dp064()
            )
            insets
        }

        binding064.tvGreeting064.text = "Halo, ${session064.getUsername()}!"
        binding064.btnViewNotes064.setOnClickListener {
            // TODO: arahkan ke layar List Catatan (Bagian #4) setelah tersedia.
            Toast.makeText(this, "Layar list catatan belum tersedia", Toast.LENGTH_SHORT).show()
        }
        binding064.btnLogout064.setOnClickListener { confirmLogout064() }
    }

    override fun onResume() {
        super.onResume()
        if (::binding064.isInitialized) loadNoteCount064()
    }

    // Jumlah catatan hanya milik user yang sedang login.
    private fun loadNoteCount064() {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(this@MainActivity).noteDao066()
            val count = dao.getAllByUser066(session064.getUserId()).size
            binding064.tvNoteCount064.text = count.toString()
        }
    }

    private fun confirmLogout064() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                session064.clear()
                goToLogin064()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun goToLogin064() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun Int.dp064(): Int = (this * resources.displayMetrics.density).toInt()
}
