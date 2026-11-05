package com.uts.catatanharian.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Memberi padding sebesar system bar (status bar / navigation bar) agar konten tidak tertutup.
fun View.applySystemBarsPadding064() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars064 = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars064.left, bars064.top, bars064.right, bars064.bottom)
        insets
    }
}
