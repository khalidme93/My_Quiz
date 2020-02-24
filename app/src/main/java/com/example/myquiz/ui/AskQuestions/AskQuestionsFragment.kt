package com.example.myquiz.ui.AskQuestions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.myquiz.Comment
import com.example.myquiz.CustomListViewAdapter
import com.example.myquiz.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myquiz.ui.AskQuestions.AskQuestionsFragment as AskQuestionsFragment

class AskQuestionsFragment : Fragment() {

    lateinit var mDatabase : DatabaseReference
    val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser
    private val myRef = FirebaseDatabase.getInstance().getReference("forum")
    private lateinit var listView: ListView
    private val list = mutableListOf<Comment>()
    private lateinit var adapter: CustomListViewAdapter
    private lateinit var askQuestionsViewModel: AskQuestionsViewModel
    private lateinit var my_context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        askQuestionsViewModel =
            ViewModelProviders.of(this).get(AskQuestionsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_ask_questions, container, false)
        listView = root.findViewById<ListView>(R.id.items_list)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user!!.uid
        this.my_context = root.context
        //reference for FAB
        val fab = root.findViewById<FloatingActionButton>(R.id.fab)

        adapter = CustomListViewAdapter(this.my_context, list!!)
        listView!!.setAdapter(adapter)


        //Adding click listener for FAB
        fab.setOnClickListener {
            //Show Dialog here to add new Item
            addNewItemDialog()
        }

        firebaseListener()


        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if(document.data?.get("Admin") as Boolean)
                listViewClickListener()

        }
            .addOnFailureListener { exception ->
                Log.w("API-Page", "Error getting documents.", exception)
            }

        return root
    }

    private fun addNewItemDialog() {
        val alert = AlertDialog.Builder(this.my_context)
        val itemEditText = EditText(this.my_context)
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

    //for admins
    private fun listViewClickListener(){
        listView.setOnItemClickListener { parent, view, position, id ->
            val alert = AlertDialog.Builder(this.my_context)
            val itemEditText = EditText(this.my_context)
            val commentItem = list.get(position)

            itemEditText.setText(commentItem.response)
            alert.setMessage("response")
            alert.setTitle("write the response!")
            alert.setView(itemEditText)
            alert.setPositiveButton("Submit") { dialog, positiveButton ->

                commentItem.response = itemEditText.text.toString()
                myRef.child(commentItem.objectId.toString()).child("response").setValue(commentItem.response)

                dialog.dismiss()
            }
            alert.show()
        }
    }
}