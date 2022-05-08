package code.with.me.testroomandnavigationdrawertest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.databinding.DbItemsBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class RecyclerViewAdapter(private val clickListener: (Int) -> Unit, private val titleList: List<String>, private val textList: List<String>, private val image: List<String>, private val draw: List<String>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


    class ViewHolder(binding: DbItemsBinding): RecyclerView.ViewHolder(binding.root){
        val titleHolder = binding.titleID
        val textHolder = binding.textID
        val cardView = binding.RecycleCardView
        val imageView = binding.imageV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DbItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.textHolder.text = textList[position]
//        holder.titleHolder.text = titleList[position]
//        holder.cardView.setOnClickListener {
//            clickListener(position)
//
//        }
        with(holder) {
            titleHolder.filters += InputFilter.LengthFilter(15)
            textHolder.filters += InputFilter.LengthFilter(20)
            titleHolder.text = titleList[position]
            textHolder.text = textList[position]
            if (image[position].isNotEmpty()) {
                Picasso.get().load(Uri.parse(image[position])).into(imageView)
            } else {
                Picasso.get().load(Uri.parse(draw[position])).into(imageView)
            }
            cardView.setOnClickListener {
                clickListener(position)
            }
        }
    }


    override fun getItemCount(): Int {
        return titleList.size
    }
}