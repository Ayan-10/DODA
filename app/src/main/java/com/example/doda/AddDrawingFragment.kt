package com.example.doda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.doda.Database.DrawingApplication
import com.example.doda.Model.Drawing
import com.example.doda.ViewModel.DrawingViewModel
import com.example.doda.ViewModel.DrawingViewModelFactory
import com.example.doda.databinding.FragmentAddDrawingBinding
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddDrawingFragment : Fragment() {

    private var _binding: FragmentAddDrawingBinding? = null

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

        _binding = FragmentAddDrawingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val date = sdf.parse(currentDate)
        val prettyTime = PrettyTime(Locale.getDefault())
        val ago: String = prettyTime.format(date)
        Log.d("addDrawing", "onViewCreated: "+ago)

        binding.buttonSave.setOnClickListener {
            val drawingName = binding.editName.text.toString()
            val thumbnail = binding.editThumb.text.toString()
            Log.d("addDrawing", "onViewCreated: $thumbnail")

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())

            val drawing = Drawing(drawingName = drawingName, additionTime = currentDate, thumbnail = thumbnail,
                countMarker = 0, markers = emptyList())
            drawingViewModel.insert(drawing)

            val bundle = bundleOf(
                "id" to drawing.id,
                "name" to drawing.drawingName,
                "date" to drawing.additionTime,
                "thumb" to drawing.thumbnail,
                "count" to drawing.countMarker,
                "list" to drawing.markers
            )

            findNavController().navigate(R.id.action_AddDrawingFragment_to_drawingFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}