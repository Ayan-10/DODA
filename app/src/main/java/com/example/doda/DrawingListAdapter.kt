package com.example.doda.ViewModel

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.doda.Model.Drawing
import com.example.doda.R
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class DrawingListAdapter(private val listener: DrawingAdapter, val context: Context) : ListAdapter<Drawing, DrawingListAdapter.DrawingViewHolder>(WORDS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawingViewHolder {
        return DrawingViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DrawingViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener, context)
    }

    class DrawingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnailView: ImageView = itemView.findViewById(R.id.thumb)
        private val nameView: TextView = itemView.findViewById(R.id.txt_name)
        private val additionTimeView: TextView = itemView.findViewById(R.id.txt_addition_time)
        private val markersView: TextView = itemView.findViewById(R.id.txt_markers)
        private  val deleteView: ImageView = itemView.findViewById(R.id.delete)
        private  val drawingView: CardView = itemView.findViewById(R.id.card_drawing)

        fun bind(drawing: Drawing, listener: DrawingAdapter, context: Context) {

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val date = sdf.parse(drawing.additionTime)
            val prettyTime = PrettyTime(Locale.getDefault())
            val ago: String = prettyTime.format(date)

            Glide
                .with(itemView)
                .load(drawing.thumbnail)
                .centerCrop()
                .into(thumbnailView)

            nameView.text = drawing.drawingName
            additionTimeView.text = "Addition time:  $ago"
            markersView.text = "Number of Markers:  "+drawing.countMarker
            deleteView.setOnClickListener {
                listener.onDeleteClicked(drawing)
            }
            drawingView.setOnClickListener {
                listener.onDrawingClicked(drawing)
            }
        }

        companion object {
            fun create(parent: ViewGroup): DrawingViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.drawing_item_layout, parent, false)
                return DrawingViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<Drawing>() {
            override fun areItemsTheSame(oldItem: Drawing, newItem: Drawing): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Drawing, newItem: Drawing): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}

interface DrawingAdapter {
    fun onDeleteClicked(drawing: Drawing)
    fun onDrawingClicked(drawing: Drawing)
}
