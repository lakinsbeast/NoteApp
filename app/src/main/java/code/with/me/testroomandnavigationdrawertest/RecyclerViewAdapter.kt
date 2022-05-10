package code.with.me.testroomandnavigationdrawertest

import android.net.Uri
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import code.with.me.testroomandnavigationdrawertest.databinding.DbItemsBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class RecyclerViewAdapter(private val clickListener: (Int) -> Unit, private val titleList: List<String>, private val textList: List<String>, private val cameraImg: List<String>, private val draw: List<String>, private val image: List<String>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


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
            if (cameraImg[position].isNotEmpty()) {
                Picasso.get().load(Uri.parse(cameraImg[position])).resize(120, 170).transform(
                    RoundedCornersTransformation(15,16)
                ).into(imageView)
            } else {
                if (draw[position].isNotEmpty()) {
                    Picasso.get().load(Uri.parse(draw[position])).resize(120, 170).transform(RoundedCornersTransformation(15,16)).into(imageView)
                } else {
                    if (image[position].isNotEmpty()) {
                        Picasso.get().load(Uri.parse(image[position])).resize(120, 170).transform(RoundedCornersTransformation(15,16)).into(imageView)
                    }
                }
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