package code.with.me.testroomandnavigationdrawertest.ui

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import code.with.me.testroomandnavigationdrawertest.data.data_classes.DataClassAdapter
import code.with.me.testroomandnavigationdrawertest.databinding.DbItemsBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


//class DatabaseRVAdapter(private val dataClassAdapter: DataClassAdapter): RecyclerView.Adapter<DatabaseRVAdapter.ViewHolder>() {
//
//    class ViewHolder(binding: DbItemsBinding): RecyclerView.ViewHolder(binding.root){
//        val titleHolder = binding.titleID
//        val textHolder = binding.textID
//        val cardView = binding.RecycleCardView
////        val imageView = binding.imageV
//
//    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = DbItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        with(holder) {
////            cardView.radius = 100F
//            ViewCompat.setTransitionName(holder.titleHolder, holder.textHolder.toString())
//            if (dataClassAdapter.notesArray[position].colorCard.isNotEmpty()) {
//                if (dataClassAdapter.notesArray[position].colorCard == "#9575CD") {
////                    cardView.setCardBackgroundColor(Color.parseColor(dataClassAdapter.notesArray[position].colorCard))
//                    titleHolder.setTextColor(Color.WHITE)
//                    textHolder.setTextColor(Color.parseColor("#BDBDBD"))
//                } else if (dataClassAdapter.notesArray[position].colorCard == "#B0BEC5") {
////                    cardView.setCardBackgroundColor(Color.parseColor(dataClassAdapter.notesArray[position].colorCard))
//                    titleHolder.setTextColor(Color.WHITE)
//                    textHolder.setTextColor(Color.parseColor("#BDBDBD"))
//                } else {
////                    cardView.setCardBackgroundColor(Color.parseColor(dataClassAdapter.notesArray[position].colorCard))
//                }
//
//
//            }
////            cardView.setContentPadding(0,10,0,10)
//            titleHolder.text = dataClassAdapter.notesArray[position].titleNote
//            textHolder.text = dataClassAdapter.notesArray[position].textNote
//            if (dataClassAdapter.notesArray[position].colorCard.isNotEmpty()) {
//                Picasso.get().load(Uri.parse(dataClassAdapter.notesArray[position].colorCard)).resize(120, 170).transform(
//                    RoundedCornersTransformation(15,8)
//                ).into(imageView)
//            } else {
//                if (dataClassAdapter.notesArray[position].imageById.isNotEmpty()) {
//                    Picasso.get().load(Uri.parse(dataClassAdapter.notesArray[position].imageById)).resize(120, 170)
//                        .transform(RoundedCornersTransformation(15, 8)).into(imageView)
//                } else {
//                    if (dataClassAdapter.notesArray[position].paintUrl.isNotEmpty()){
//                        Picasso.get().load(Uri.parse(dataClassAdapter.notesArray[position].paintUrl)).resize(120, 170)
//                            .transform(RoundedCornersTransformation(15,8)).into(imageView)
//
//                    }
//                }
//            }
//
//            cardView.setOnClickListener {
//                dataClassAdapter.clickListener(position)
//            }
//        }
//    }
//
//
//    override fun getItemCount(): Int {
//        return dataClassAdapter.notesArray.size
//    }
//
//
//}