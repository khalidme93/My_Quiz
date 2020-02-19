package com.example.myquiz

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*


class API : AppCompatActivity() {

    private val myRef = FirebaseDatabase.getInstance().getReference("forum")
    private lateinit var listView: ListView
    private val list = mutableListOf<Comment>()
    private lateinit var adapter: CustomListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api)

        listView = findViewById<ListView>(R.id.items_list)

        //reference for FAB
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        adapter = CustomListViewAdapter(this, list!!)
        listView!!.setAdapter(adapter)


        //Adding click listener for FAB
        fab.setOnClickListener {
            //Show Dialog here to add new Item
            addNewItemDialog()
        }

        firebaseListener()

        listView.setOnItemClickListener { parent, view, position, id ->
            val alert = AlertDialog.Builder(this)
            val itemEditText = EditText(this)
            val commentItem = list.get(position)

            itemEditText.setText(commentItem.response)
            alert.setMessage("response")
            alert.setTitle("write the response!")
            alert.setView(itemEditText)
            alert.setPositiveButton("Submit") { dialog, positiveButton ->

                commentItem.response = itemEditText.text.toString()
                myRef.child(commentItem.objectId.toString()).child("response").setValue(commentItem.response)

                Toast.makeText(this, "Item saved with ID " + commentItem.objectId, Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
            alert.show()
        }
    }

    private fun addNewItemDialog() {
        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)
        alert.setMessage("Add A Comment")
        alert.setTitle("Enter Your Problem")
        alert.setView(itemEditText)
        alert.setPositiveButton("Submit") { dialog, positiveButton ->
            val commentItem = com.example.myquiz.Comment()
            commentItem.itemText = itemEditText.text.toString()
            commentItem.response = ""
            //We first make a push so that a new item is made with a unique ID
            val newItem = myRef.push()
            commentItem.objectId = newItem.key
            //then, we used the reference to set the value on that ID
            newItem.setValue(commentItem)
            dialog.dismiss()
            Toast.makeText(this, "Item saved with ID " + commentItem.objectId, Toast.LENGTH_SHORT)
                .show()
        }
        alert.show()


    }


    private fun firebaseListener(){
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("API-SITE", "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val comment = dataSnapshot.getValue(Comment::class.java)
                if(comment != null) {
                    list.add(comment)
                    adapter.notifyDataSetChanged()
                }
                // ...
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("API-SITE", "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val comment = dataSnapshot.getValue(Comment::class.java)
                if(comment != null) {
                    adapter.notifyDataSetChanged()
                }

                // ...
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("API-SITE", "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("API-SITE", "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue(Comment::class.java)
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("API-SITE", "postComments:onCancelled", databaseError.toException())

            }
        }
        myRef.addChildEventListener(childEventListener)
    }
}




