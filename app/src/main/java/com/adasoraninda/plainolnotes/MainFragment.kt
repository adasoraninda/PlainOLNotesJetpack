package com.adasoraninda.plainolnotes

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adasoraninda.plainolnotes.data.NoteEntity
import com.adasoraninda.plainolnotes.databinding.FragmentMainBinding

class MainFragment : Fragment(), NotesListAdapter.ListItemListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var adapter: NotesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

        requireActivity().title = getString(R.string.app_name)

        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        with(binding.listNotes) {
            setHasFixedSize(true)
            val divider = DividerItemDecoration(context, LinearLayoutManager(context).orientation)
            addItemDecoration(divider)
        }

        viewModel.notesList?.observe(viewLifecycleOwner) { notes ->
            Log.i(TAG, notes.toString())
            adapter = NotesListAdapter(notes, this@MainFragment)
            binding.listNotes.adapter = adapter
            binding.listNotes.layoutManager = LinearLayoutManager(activity)

            val selectedNotes =
                savedInstanceState?.getParcelableArrayList<NoteEntity>(SELECTED_NOTES_KEY)
            adapter.selectedNotes.addAll(selectedNotes ?: emptyList())
        }

        binding.fabAdd.setOnClickListener {
            editNote(NEW_NOTE_ID)
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::adapter.isInitialized) {
            outState.putParcelableArrayList(SELECTED_NOTES_KEY, adapter.selectedNotes)
        }
        super.onSaveInstanceState(outState)
    }

    override fun editNote(noteId: Int) {
        Log.i(TAG, noteId.toString())

        val action = MainFragmentDirections.navMainToEditor(noteId)
        findNavController().navigate(action)
    }

    override fun onItemSelectionChange() {
        requireActivity().invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuId = if (this::adapter.isInitialized && adapter.selectedNotes.isNotEmpty()) {
            R.menu.menu_main_selected_items
        } else {
            R.menu.menu_main
        }

        inflater.inflate(menuId, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sample_data -> addSampleData()
            R.id.action_delete -> deleteSelectedNotes()
            R.id.action_delete_all -> deleteAllNotes()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun deleteAllNotes(): Boolean {
        viewModel.deleteAllNotes()
        return true
    }

    private fun addSampleData(): Boolean {
        viewModel.addSampleData()
        return true
    }

    private fun deleteSelectedNotes(): Boolean {
        viewModel.deleteNotes(adapter.selectedNotes)
        Handler(Looper.getMainLooper()).postDelayed({
            adapter.selectedNotes.clear()
            requireActivity().invalidateOptionsMenu()
        }, 100)
        return true
    }

}