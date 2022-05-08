package code.with.me.testroomandnavigationdrawertest.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.NoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.RecyclerViewAdapter
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val idsList = ArrayList<Int>()
    private val titlesList = ArrayList<String>()
    private val textList = ArrayList<String>()
    private var imageInRecycler = ArrayList<String>()
    private var drawInRecycler = ArrayList<String>()

//    private var isFabOpen: Boolean = false



    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "SimpleNote"


        val noteViewModel: NoteViewModel by viewModels {
            NoteViewModelFactory((application as NotesApplication).repo)
        }

        binding.recyc.layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerViewAdapter({
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", it)
            Log.d("id", it.toString())
            startActivity(intent)
        },titlesList, textList, imageInRecycler, drawInRecycler)
        binding.recyc.adapter = adapter

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        noteViewModel.allNotes.observe(this) {
            it.forEach { i ->
                if (!(i.id in idsList && i.titleNote in titlesList && i.textNote in textList
                            && i.imageById in imageInRecycler && i.paintUrl in drawInRecycler)) {
                idsList.add(i.id)
                titlesList.add(i.titleNote)
                textList.add(i.textNote)
                imageInRecycler.add(i.imageById)
                drawInRecycler.add(i.paintUrl)
                Log.d("viewmodel", idsList.toString())
                adapter.notifyDataSetChanged()
                }
            }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}