package com.example.catatanku.home.action

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.catatanku.databinding.ActivityCreateNotesBinding
import com.example.catatanku.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class CreateNotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNotesBinding
    var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null
    var firebaseFirestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNotesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarofcreatenote)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseAuth=FirebaseAuth.getInstance()
        firebaseFirestore=FirebaseFirestore.getInstance()
        firebaseUser=FirebaseAuth.getInstance().currentUser

        binding.savenote.setOnClickListener {
            val title: String = binding.createtitleofnote.text.toString().trim()
            val content: String = binding.createcontentofnote.text.toString().trim()
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(applicationContext, "Tidak Boleh Kosong", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.progressbarofcreatenote.visibility = View.VISIBLE
                val documentReference = firebaseFirestore!!.collection("notes").document(
                    firebaseUser!!.uid
                ).collection("myNotes").document()
                val note: MutableMap<String, Any> = HashMap()
                note["title"] = title
                note["content"] = content
                documentReference.set(note).addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Catatan berhasil disimpan",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(Intent(this, HomeActivity::class.java))
                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Gagal menyimpan catatan",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    binding.progressbarofcreatenote.visibility = View.INVISIBLE
                }
            }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}