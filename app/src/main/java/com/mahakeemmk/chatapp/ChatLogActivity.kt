package com.mahakeemmk.chatapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_row_from.view.*
import kotlinx.android.synthetic.main.chat_row_to.view.*

class ChatLogActivity : AppCompatActivity() {

    private val buttonClick: AlphaAnimation = AlphaAnimation(1F, 0.8F)

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val userName = intent.getStringExtra("USER_KEY")
        val userId = intent.getStringExtra("USER_ID")

        supportActionBar?.title = userName

        chat_log.adapter = adapter

        send_button.setOnClickListener {
            it.startAnimation(buttonClick)
            val text =  chat_editText.text.toString()
            val fromId = FirebaseAuth.getInstance().currentUser?.uid
            val toId = userId

            val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId").push()

            val toref = FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId").push()

            val chatMessage = ChatMessage(ref.key,text,fromId,toId,System.currentTimeMillis()/1000)
            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("send message","successful")
                    chat_editText.text.clear()
                    chat_log.scrollToPosition(adapter.itemCount-1)
                }

            toref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("send message","successful")
                }

        }
        val fromId = FirebaseAuth.getInstance().currentUser?.uid
        val toId = userId
        val adapter = GroupAdapter<ViewHolder>()

        val ref = FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage!=null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text))
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        chat_log.adapter = adapter

    }

}

class ChatMessage(val id:String?,val text:String,val fromId:String?,val toId:String,timeStamp:Long) {
    constructor() : this("","","","",-1)
}

class ChatFromItem(val text:String):Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_row_from
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from.text = text
    }
}

class ChatToItem(val text:String):Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_row_to
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to.text = text
    }
}
