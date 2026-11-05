package com.uts.catatanharian.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uts.catatanharian.data.Note064
import com.uts.catatanharian.databinding.ItemNote064Binding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adapter RecyclerView untuk daftar catatan.
class NoteAdapter064(
    private val onClick064: (Note064) -> Unit,
    private val onDelete064: (Note064) -> Unit
) : ListAdapter<Note064, NoteAdapter064.ViewHolder064>(Diff064) {

    private val dateFormat064 = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    inner class ViewHolder064(val binding064: ItemNote064Binding) :
        RecyclerView.ViewHolder(binding064.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder064 {
        val binding064 = ItemNote064Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder064(binding064)
    }

    override fun onBindViewHolder(holder: ViewHolder064, position: Int) {
        val note064 = getItem(position)
        with(holder.binding064) {
            tvJudul064.text = note064.judul
            tvIsi064.text = note064.isi
            tvTanggal064.text = dateFormat064.format(Date(note064.tanggal))
            root.setOnClickListener { onClick064(note064) }
            btnDelete064.setOnClickListener { onDelete064(note064) }
        }
    }

    private object Diff064 : DiffUtil.ItemCallback<Note064>() {
        override fun areItemsTheSame(old: Note064, new: Note064) = old.id == new.id
        override fun areContentsTheSame(old: Note064, new: Note064) = old == new
    }
}
