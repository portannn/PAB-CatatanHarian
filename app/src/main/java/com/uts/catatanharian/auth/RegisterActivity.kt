package com.uts.catatanharian.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.uts.catatanharian.data.AppDatabase
import com.uts.catatanharian.data.User064
import com.uts.catatanharian.util.applySystemBarsPadding064
import com.uts.catatanharian.databinding.ActivityRegister064Binding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding064: ActivityRegister064Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding064 = ActivityRegister064Binding.inflate(layoutInflater)
        setContentView(binding064.root)
        binding064.root.applySystemBarsPadding064()

        binding064.btnRegister064.setOnClickListener { doRegister064() }
        binding064.tvToLogin064.setOnClickListener { finish() }
    }

    private fun doRegister064() {
        val username064 = binding064.etUsername064.text.toString().trim()
        val password064 = binding064.etPassword064.text.toString()
        val confirm064 = binding064.etConfirm064.text.toString()

        binding064.tilUsername064.error = AuthValidator064.validateUsername064(username064)
        binding064.tilPassword064.error = AuthValidator064.validatePassword064(password064)
        binding064.tilConfirm064.error = AuthValidator064.validateConfirm064(password064, confirm064)
        if (binding064.tilUsername064.error != null ||
            binding064.tilPassword064.error != null ||
            binding064.tilConfirm064.error != null) return

        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(this@RegisterActivity).userDao064()
            if (dao.getByUsername064(username064) != null) {
                binding064.tilUsername064.error = "Username sudah dipakai"
                return@launch
            }
            dao.insertUser064(User064(username = username064, password = AuthValidator064.hash064(password064)))
            Toast.makeText(this@RegisterActivity, "Registrasi berhasil, silakan login", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
