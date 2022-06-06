package com.example.catatanku.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.catatanku.MainActivity
import com.example.catatanku.R
import com.example.catatanku.databinding.ActivityHomeBinding
import com.example.catatanku.databinding.NotesLayoutBinding
import com.example.catatanku.detail.NoteDetailActivity
import com.example.catatanku.home.action.CreateNotesActivity
import com.example.catatanku.home.action.EditNoteActivity
import com.example.catatanku.model.NotesModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var firebaseAuth: FirebaseAuth? = null
    private var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    var firebaseUser: FirebaseUser? = null
    var firebaseFirestore: FirebaseFirestore? = null

    var noteAdapter: FirestoreRecyclerAdapter<NotesModel, NoteViewHolder>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().currentUser;
        firebaseFirestore=FirebaseFirestore.getInstance();
        val sharedPreferences = getSharedPreferences("map", MODE_PRIVATE)
        var img = sharedPreferences.getString("img", "")
        Glide
            .with(applicationContext)
            .load(img)
            .centerCrop()
            .into(binding.profileImage);
        binding.fab.setOnClickListener{
            startActivity(Intent(this, CreateNotesActivity::class.java))
        }
        binding.profileImage.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        val query: Query = firebaseFirestore!!.collection("notes").document(firebaseUser!!.uid)
            .collection("myNotes").orderBy("title", Query.Direction.ASCENDING)

        val allusernotes: FirestoreRecyclerOptions<NotesModel> =
            FirestoreRecyclerOptions.Builder<NotesModel>().setQuery(
                query,
                NotesModel::class.java
            ).build()
        noteAdapter =
            object : FirestoreRecyclerAdapter<NotesModel, NoteViewHolder>(allusernotes) {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onBindViewHolder(
                    @NonNull noteViewHolder: NoteViewHolder,
                    i: Int,
                    @NonNull firebasemodel: NotesModel
                ) {
                    val popupbutton: ImageView =
                        noteViewHolder.itemView.findViewById(R.id.menupopbutton)
                    val colourcode = getRandomColor()
                    noteViewHolder.mnote.setBackgroundColor(
                        noteViewHolder.itemView.resources.getColor(
                            colourcode,
                            null
                        )
                    )
                    Log.e("root", firebasemodel.title.toString())
                    Log.e("root", firebasemodel.content.toString())
                    noteViewHolder.notetitle.text = firebasemodel.title
                    noteViewHolder.notecontent.text = firebasemodel.content
                    val docId = noteAdapter!!.snapshots.getSnapshot(i).id
                    noteViewHolder.itemView.setOnClickListener { v ->
                        val intent = Intent(v.context, NoteDetailActivity::class.java)
                        intent.putExtra("title", firebasemodel.title)
                        intent.putExtra("content", firebasemodel.content)
                        intent.putExtra("noteId", docId)
                        v.context.startActivity(intent)

                    }
                    popupbutton.setOnClickListener {v ->
                        val popupMenu = PopupMenu(v.context, v)
                        popupMenu.gravity = Gravity.END
                        popupMenu.menu.add("Edit")
                            .setOnMenuItemClickListener {
                                val intent = Intent(v.context, EditNoteActivity::class.java)
                                intent.putExtra("title", firebasemodel.title)
                                intent.putExtra("content", firebasemodel.content)
                                intent.putExtra("noteId", docId)
                                v.context.startActivity(intent)
                                false
                            }
                        popupMenu.menu.add("Delete")
                            .setOnMenuItemClickListener {
                                val builder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@HomeActivity)
                                builder.setTitle("Delete")
                                builder.setMessage("Are you sure you want to delete?")
                                builder.setPositiveButton("Yes"
                                ) { _, _ ->
                                    val documentReference =
                                        firebaseFirestore!!.collection("notes")
                                            .document(
                                                firebaseUser!!.uid
                                            ).collection("myNotes").document(docId)
                                    documentReference.delete().addOnSuccessListener {
                                        Toast.makeText(
                                            v.context,
                                            "This note is deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            v.context,
                                            "Failed To Delete",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                builder.setNegativeButton("No"
                                ) { _, _ -> }
                                builder.create().show()
                                false
                            }
                        popupMenu.show()
                    }
                }

                @NonNull
                override fun onCreateViewHolder(
                    parent: ViewGroup, viewType: Int): NoteViewHolder {
                    val binding = NotesLayoutBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                    return NoteViewHolder(binding)
                }
            }

        binding.recyclerview.setHasFixedSize(true)
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerview.layoutManager = staggeredGridLayoutManager
        binding.recyclerview.adapter = noteAdapter
    }
    class NoteViewHolder( binding: NotesLayoutBinding) :RecyclerView.ViewHolder(binding.root) {
        val notetitle: TextView
        val notecontent: TextView
        val mnote: LinearLayout


        init {
            notetitle = binding.notetitle
            notecontent = binding.notecontent
            mnote = binding.note

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.translate_anim)
            mnote.animation = animation
        }
    }

    override fun onStart() {
        super.onStart()
        noteAdapter!!.startListening()
    }
    private fun getRandomColor(): Int {
        val colorcode: MutableList<Int> = ArrayList()
        colorcode.add(R.color.gray)
        colorcode.add(R.color.pink)
        colorcode.add(R.color.lightgreen)
        colorcode.add(R.color.skyblue)
        colorcode.add(R.color.color1)
        colorcode.add(R.color.color2)
        colorcode.add(R.color.color3)
        colorcode.add(R.color.color4)
        colorcode.add(R.color.color5)
        colorcode.add(R.color.green)
        val random = Random()
        val number: Int = random.nextInt(colorcode.size)
        return colorcode[number]
    }
}