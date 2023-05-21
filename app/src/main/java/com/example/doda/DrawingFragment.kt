package com.example.doda

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.doda.Database.DrawingApplication
import com.example.doda.Model.Drawing
import com.example.doda.Model.Marker
import com.example.doda.ViewModel.DrawingViewModel
import com.example.doda.ViewModel.DrawingViewModelFactory
import com.example.doda.databinding.FragmentDrawingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DrawingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrawingFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var gestureDetector: GestureDetector? = null
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    var tapped = false


    private var _binding: FragmentDrawingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val drawingViewModel: DrawingViewModel by viewModels {
        DrawingViewModelFactory((requireActivity().application as DrawingApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getInt("id")
        val name = arguments?.getString("name")
        val additionDate = arguments?.getString("date")
        val thumbnail = arguments?.getString("thumb")
        val countMarkers = arguments?.getInt("count")
        var markers: List<Marker>? = arguments?.getParcelableArrayList("list",Marker::class.java)
        if (markers == null) {
            markers = emptyList<Marker>()
        }
        val newMarkers: MutableList<Marker> = markers.toMutableList()
        Log.d("drawing", "onViewCreated: "+markers!!.size)

        binding.textName.text = name

        binding.btnShow.setOnClickListener {
            val v = binding.imageView
            if (markers.isNotEmpty()) {
                for (l in markers) {
                    val drawable = v.drawable
                    val bm: Bitmap = drawable!!.toBitmap(v.getWidth(),
                        v.getHeight(), Bitmap.Config.ARGB_8888)
                    var cv = Canvas(bm);
                    cv.drawBitmap(bm, 0f, 0f, null);

                    val paint = Paint()
                    paint.setStyle(Paint.Style.FILL)
                    paint.setColor(getResources().getColor(R.color.red))
                    cv.drawCircle(l.x.toFloat(), l.y.toFloat(), 20f, paint)

                    v.setImageBitmap(bm)
                }
            }
            binding.btnShow.visibility = View.INVISIBLE
        }

        Glide
            .with(this)
            .load(thumbnail)
            .centerCrop()
            .into(binding.imageView)


        binding.btnExit.setOnClickListener {
            findNavController().navigate(R.id.action_drawingFragment_to_DrawingListFragment)
        }

        binding.btnSave.setOnClickListener {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            Log.d("drawing", "onViewCreated: "+newMarkers.size+" ug "+currentDate)
            val drawing = Drawing(
                id = id!!,
                drawingName = name!!,
                currentDate!!,
                thumbnail!!,
                newMarkers.size,
                newMarkers
            )
            drawingViewModel.update(drawing)
        }
        gestureDetector = GestureDetector(
            activity,
            GestureListener(
                binding.imageView,
                requireContext(),
                markers,
                newMarkers
            )
        )
        mScaleGestureDetector = ScaleGestureDetector(requireContext(),
            ScaleListener(
                binding.imageView,
                mScaleFactor
            )
        )

        binding.imageView.setOnTouchListener(OnTouchListener { v, event -> // TODO Auto-generated method stub
            gestureDetector!!.onTouchEvent(event!!)
            mScaleGestureDetector!!.onTouchEvent(event)
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DrawingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DrawingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class GestureListener(
        var view: ImageView,
        val context: Context,
        val markers: List<Marker>?,
        val newMarkers: MutableList<Marker>
    ) : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        // event when double tap occurs
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val xCoordinate = e.getX() as Float
            val yCoordinate = e.getY() as Float
            val radius: Float = 20F
            val doesContain : Int = containsIn(xCoordinate,yCoordinate,markers)
            if(doesContain > -1){
                Toast.makeText(context, "Marker already added", Toast.LENGTH_SHORT).show()
            } else {
                val mView: View = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_form, null, false)
                val edtName: EditText = mView.findViewById(R.id.edit_name)
                val edtDetails: EditText = mView.findViewById(R.id.edit_details)

                val btnSave: Button = mView.findViewById(R.id.btnSave)
                val btnRemove: Button = mView.findViewById(R.id.btnCancel)

                val popUp = PopupWindow(context)
                popUp.isTouchable = true
                popUp.isFocusable = true
                popUp.contentView = mView

                popUp.width = LinearLayout.LayoutParams.MATCH_PARENT
                popUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
                popUp.isOutsideTouchable = false
                popUp.isFocusable = true
                popUp.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                popUp.showAtLocation(view, Gravity.CENTER, 0, 0);

                btnSave.setOnClickListener {
                    val name = edtName.text.toString()
                    val details = edtDetails.text.toString()
                    val marker = Marker(xCoordinate.toDouble(), yCoordinate.toDouble(), name, details)
                    newMarkers.add(marker)
                    popUp.dismiss()

                    val drawable = view.getDrawable()
                    val bitmap: Bitmap = drawable.toBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)

                    var cv = Canvas(bitmap);
                    cv.drawBitmap(bitmap, 0f, 0f, null);

                    val paint = Paint()
                    paint.setStyle(Paint.Style.FILL)
                    paint.setColor(context.getResources().getColor(R.color.red))
                    cv.drawCircle(xCoordinate, yCoordinate, radius, paint)

                    view.setImageBitmap(bitmap)
                }

                btnRemove.setOnClickListener {
                    popUp.dismiss()
                }
            }

            return true
        }

        private fun containsIn(xCurrent: Float, yCurrent: Float, markers: List<Marker>?): Int {
            var index = -1
            for (i in markers!!.indices) {
                val marker = markers[i]
                val xCoordinate = marker.x
                val yCoordinate = marker.y
                // Calculating euclidean distance
                val distance = Math.sqrt(Math.pow(xCoordinate-xCurrent, 2.0) + Math.pow(yCoordinate-yCurrent, 2.0))
                if (distance < 40) {
                    index = i
                    break
                }
            }
            return index
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            val xCoordinate = e.getX() as Float
            val yCoordinate = e.getY() as Float
            val radius: Float = 20F

            val doesContain : Int = containsIn(xCoordinate,yCoordinate,markers)
            if(doesContain > -1){
                val m = markers!![doesContain]
                Log.d("drawing", "onSingleTapUp: "+m.name)
                showBottomSheetDialog(m.name, m.details)
            }
            return true
        }

        private fun showBottomSheetDialog(name: String, details: String) {
            val bottomSheetDialog = BottomSheetDialog(context)
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)

            val nameView = bottomSheetDialog.findViewById<TextView>(R.id.textView)
            val detailsView = bottomSheetDialog.findViewById<TextView>(R.id.textView2)

            nameView!!.text = name
            detailsView!!.text = details


            bottomSheetDialog.show()

        }
    }

    class ScaleListener(val imageView: ImageView, var mScaleFactor: Float) : ScaleGestureDetector.OnScaleGestureListener {

        override fun onScale(p0: ScaleGestureDetector): Boolean {
            mScaleFactor *= p0.getScaleFactor();
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }

        override fun onScaleBegin(p0: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(p0: ScaleGestureDetector) {
        }

    }
}

