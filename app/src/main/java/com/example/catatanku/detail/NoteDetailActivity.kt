package com.example.catatanku.detail


import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.catatanku.databinding.ActivityNoteDetailBinding
import com.example.catatanku.home.action.EditNoteActivity


class NoteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val data = intent
        setSupportActionBar(binding.toolbarofnotedetail);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        binding.gotoeditnote.setOnClickListener{v ->
            val intent = Intent(v.context, EditNoteActivity::class.java)
            intent.putExtra("title", data.getStringExtra("title"))
            intent.putExtra("content", data.getStringExtra("content"))
            intent.putExtra("noteId", data.getStringExtra("noteId"))
            v.context.startActivity(intent)
        }
        binding.titleofnotedetail.text = data.getStringExtra("title");
        binding.contentofnotedetail.text = data.getStringExtra("content");
    }
    override fun onOptionsItemSelected(@NonNull item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}