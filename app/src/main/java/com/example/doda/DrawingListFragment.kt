package com.example.doda

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doda.Database.DrawingApplication
import com.example.doda.Model.Drawing
import com.example.doda.ViewModel.DrawingAdapter
import com.example.doda.ViewModel.DrawingListAdapter
import com.example.doda.ViewModel.DrawingViewModel
import com.example.doda.ViewModel.DrawingViewModelFactory
import com.example.doda.databinding.FragmentDrawingListBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DrawingListFragment : Fragment(), DrawingAdapter {

    private var _binding: FragmentDrawingListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val drawingViewModel: DrawingViewModel by viewModels {
        DrawingViewModelFactory((requireActivity().application as DrawingApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDrawingListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerview
        val adapter = DrawingListAdapter(this, requireContext())
        recyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_DrawingListFragment_to_AddDrawingFragment)
        }

        drawingViewModel.allDrawing.observe(requireActivity()) { drawings ->
            // Update the cached copy of the words in the adapter.
            if(drawings.isNotEmpty()) {
                adapter.submitList(drawings)
                for (item in drawings) {
                    Log.d("drawingList", item.drawingName)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDeleteClicked(drawing: Drawing) {

        val mDialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure! You want to delete this word?")
            .setCancelable(false)
            .setPositiveButton("Delete") { dialog: DialogInterface, which: Int ->
                drawingViewModel.delete(drawing)
                dialog.dismiss()
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.dismiss() }.show()
    }

    override fun onDrawingClicked(drawing: Drawing) {

        val bundle = bundleOf(
            "id" to drawing.id,
            "name" to drawing.drawingName,
            "date" to drawing.additionTime,
            "thumb" to drawing.thumbnail,
            "count" to drawing.countMarker,
            "list" to drawing.markers
        )

        findNavController().navigate(R.id.action_DrawingListFragment_to_drawingFragment, bundle)
    }
}