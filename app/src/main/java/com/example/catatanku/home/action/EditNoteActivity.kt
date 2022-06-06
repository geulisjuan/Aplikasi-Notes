package com.example.catatanku.home.action

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.catatanku.databinding.ActivityEditNoteBinding
import com.example.catatanku.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditNoteBinding
    var firebaseFirestore: FirebaseFirestore? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val data = intent;
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().currentUser;
        setSupportActionBar(binding.toolbarofeditnote);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        binding.saveeditnote.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val newtitle: String = binding.edittitleofnote.text.toString()
                val newcontent: String = binding.editcontentofnote.text.toString()
                if (newtitle.isEmpty() || newcontent.isEmpty()) {
                    Toast.makeText(applicationContext, "Something is empty", Toast.LENGTH_SHORT)
                        .show()
                    return
                } else {
                    val documentReference = firebaseFirestore!!.collection("notes").document(
                        firebaseUser!!.uid
                    ).collection("myNotes").document(data.getStringExtra("noteId")!!)
                    val note: MutableMap<String, Any> = HashMap()
                    note["title"] = newtitle
                    note["content"] = newcontent
                    documentReference.set(note).addOnSuccessListener {
                        Toast.makeText(applicationContext, "Note is updated", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@EditNoteActivity, HomeActivity::class.java))
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Failed To update",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })


        val notetitle = data.getStringExtra("title")
        val notecontent = data.getStringExtra("content")
        binding.editcontentofnote.setText(notecontent)
        binding.edittitleofnote.setText(notetitle)
    }
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}