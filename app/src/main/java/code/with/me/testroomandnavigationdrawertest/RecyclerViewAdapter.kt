package code.with.me.testroomandnavigationdrawertest

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import code.with.me.testroomandnavigationdrawertest.Activities.MainActivity
import code.with.me.testroomandnavigationdrawertest.databinding.DbItemsBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlin.coroutines.coroutineContext

class RecyclerViewAdapter(private val clickListener: (Int) -> Unit,
                          private val titleList: List<String>, private val textList: List<String>,
                          private val cameraImg: List<String>, private val audioList: List<String>, private val draw: List<String>,
                          private val image: List<String>, private val colors: List<String>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

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
        with(holder) {
//            cardView.radius = 100F
            ViewCompat.setTransitionName(holder.titleHolder, holder.textHolder.toString())
            if (colors.isNotEmpty()) {
                if (colors[position] == "#9575CD") {
                    cardView.setCardBackgroundColor(Color.parseColor(colors[position]))
                    titleHolder.setTextColor(Color.WHITE)
                    textHolder.setTextColor(Color.parseColor("#BDBDBD"))
                } else if (colors[position] == "#B0BEC5") {
                    cardView.setCardBackgroundColor(Color.parseColor(colors[position]))
                    titleHolder.setTextColor(Color.WHITE)
                    textHolder.setTextColor(Color.parseColor("#BDBDBD"))
                } else {
                    cardView.setCardBackgroundColor(Color.parseColor(colors[position]))
                }


            }
            cardView.setContentPadding(0,10,0,10)
            titleHolder.text = titleList[position]
            textHolder.text = textList[position]
            if (cameraImg[position].isNotEmpty()) {
//                imageView.requestLayout()
//                imageView.layoutParams.height = 170
//                imageView.layoutParams.width = 100
//                imageView.setImageURI(Uri.parse(cameraImg[position]))
                Picasso.get().load(Uri.parse(cameraImg[position])).resize(120, 170).transform(
                    RoundedCornersTransformation(15,8)
                ).into(imageView)
            } else {
                if (image[position].isNotEmpty()) {
//                imageView.requestLayout()
//                imageView.layoutParams.height = 170
//                imageView.layoutParams.width = 100
//                imageView.setImageURI(Uri.parse(draw[position]))
                    Picasso.get().load(Uri.parse(image[position])).resize(120, 170)
                        .transform(RoundedCornersTransformation(15, 8)).into(imageView)
                } else {
                    if (draw[position].isNotEmpty()){
//                    imageView.requestLayout()
//                    imageView.layoutParams.height = 170
//                    imageView.layoutParams.width = 100
//                    imageView.setImageURI(Uri.parse(image[position]))
                        Picasso.get().load(Uri.parse(draw[position])).resize(120, 170)
                            .transform(RoundedCornersTransformation(15,8)).into(imageView)

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