package com.example.notes.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.FragmentEditNoteBinding
import com.example.notes.model.Note
import com.example.notes.viewmodel.NoteViewModel

class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true) // Enable options menu

        notesViewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)

        try {
            // Retrieve the note passed as an argument
            currentNote = requireArguments().getParcelable("note") ?: throw IllegalArgumentException("Note is missing")

            binding.editNoteTitle.setText(currentNote.noteTitle)
            binding.editNoteDesc.setText(currentNote.noteDesc)

            binding.editNoteFab.setOnClickListener {
                val noteTitle = binding.editNoteTitle.text.toString().trim()
                val noteDesc = binding.editNoteDesc.text.toString().trim()

                if (noteTitle.isNotEmpty()) {
                    val updatedNote = Note(currentNote.id, noteTitle, noteDesc)
                    notesViewModel.updateNote(updatedNote)
                    findNavController().popBackStack(R.id.homeFragment, false)
                } else {
                    Toast.makeText(requireContext(), "Please enter note title", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("EditNoteFragment", "Error: ${e.message}", e)
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete Note")
            setMessage("Do you want to delete this note?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteNote(currentNote)
                Toast.makeText(requireContext(), "Note Deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_note, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteMenu -> {
                deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
