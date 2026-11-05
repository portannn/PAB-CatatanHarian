package com.uts.catatanharian.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uts.catatanharian.MainActivity
import com.uts.catatanharian.data.AppDatabase
import com.uts.catatanharian.util.applySystemBarsPadding064
import com.uts.catatanharian.databinding.ActivityLogin064Binding
import com.uts.catatanharian.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding064: ActivityLogin064Binding
    private lateinit var session064: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        session064 = SessionManager(this)
        if (session064.isLoggedIn()) {
            goToDashboard064()
            return
        }
        binding064 = ActivityLogin064Binding.inflate(layoutInflater)
        setContentView(binding064.root)
        binding064.root.applySystemBarsPadding064()

        binding064.btnLogin064.setOnClickListener { doLogin064() }
        binding064.tvToRegister064.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin064() {
        val username064 = binding064.etUsername064.text.toString().trim()
        val password064 = binding064.etPassword064.text.toString()

        binding064.tilUsername064.error = AuthValidator064.validateUsername064(username064)
        binding064.tilPassword064.error =
            if (password064.isEmpty()) "Password tidak boleh kosong" else null
        if (binding064.tilUsername064.error != null || binding064.tilPassword064.error != null) return

        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(this@LoginActivity).userDao064()
            val user064 = dao.login064(username064, AuthValidator064.hash064(password064))
            if (user064 == null) {
                Toast.makeText(this@LoginActivity, "Username atau password salah", Toast.LENGTH_SHORT).show()
            } else {
                session064.saveSession(user064.id, user064.username)
                goToDashboard064()
            }
        }
    }

    private fun goToDashboard064() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
