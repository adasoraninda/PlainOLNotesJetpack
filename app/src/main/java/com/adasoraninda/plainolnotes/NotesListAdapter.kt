package com.adasoraninda.plainolnotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adasoraninda.plainolnotes.data.NoteEntity
import com.adasoraninda.plainolnotes.databinding.ListItemBinding


class NotesListAdapter(
    private val notesList: List<NoteEntity>,
    private val listener: ListItemListener
) :
    RecyclerView.Adapter<NotesListAdapter.ViewHolder>() {

    val selectedNotes = arrayListOf<NoteEntity>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        with(holder.binding) {
            textNote.text = note.text
            root.setOnClickListener {
                listener.editNote(note.id)
            }
            fabSelect.setOnClickListener {
                if (selectedNotes.contains(note)) {
                    selectedNotes.remove(note)
                    fabSelect.setImageResource(R.drawable.ic_notes)
                } else {
                    selectedNotes.add(note)
                    fabSelect.setImageResource(R.drawable.ic_check)
                }
                listener.onItemSelectionChange()
            }
            fabSelect.setImageResource(
                if (selectedNotes.contains(note)) {
                    R.drawable.ic_check
                } else {
                    R.drawable.ic_notes
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    interface ListItemListener {
        fun editNote(noteId: Int)
        fun onItemSelectionChange()
    }

}