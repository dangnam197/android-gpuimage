package jp.co.cyberagent.android.gpuimage.sample.adapater

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.android.gpuimage.sample.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.sample.R
import kotlinx.android.synthetic.main.item_category.view.*


class CategoryAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            if(oldItem != newItem) return true
            return false
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return false
        }
    }
    private lateinit var filter : GPUImageFilterTools.FilterList
    private var selectItem = -1
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category,
                parent,
                false
            ),
            interaction
        )
        Ads
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(position,differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: GPUImageFilterTools.FilterList) {
        filter = list
        differ.submitList(list.names)
    }

    inner class ViewHolder constructor(itemView: View, private val interaction: Interaction?) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, filter.filters[adapterPosition])
                selectItem = adapterPosition
                notifyDataSetChanged()
            }
        }
        fun bind(position: Int,item: String) = with(itemView) {
            itemView.tv_item_category.text = item
            if(selectItem == position){
                itemView.view_item_category.visibility = View.VISIBLE
                itemView.tv_item_category.setTextColor(Color.parseColor("#CBEF63"))
            }else{
                itemView.view_item_category.visibility = View.INVISIBLE
                itemView.tv_item_category.setTextColor(Color.WHITE)
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: GPUImageFilterTools.FilterType)
    }
}