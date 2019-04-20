package com.mahakeemmk.chatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class Messages : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        supportActionBar?.title = "Latest Messages"

        val uid = FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent = Intent(this,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val adapter = GroupAdapter<ViewHolder>()
        val latestMessageMap = HashMap<String,ChatMessage>()


        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$fromId")
        ref.addChildEventListener(object:ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return

                latestMessageMap[p0.key!!] = chatMessage
                adapter.clear()
                latestMessageMap.values.forEach{
                    adapter.add(LatestMessageRow(it))
                }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return

                latestMessageMap[p0.key!!] = chatMessage
                adapter.clear()
                latestMessageMap.values.forEach{
                    adapter.add(LatestMessageRow(it))
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

        latest_messages_recycler.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this,ChatLogActivity::class.java)
            var row = item as LatestMessageRow
            intent.putExtra("USER_KEY",row.partner?.name)
            intent.putExtra("USER_ID",row.partner?.uid)
            startActivity(intent)
        }

        new_message_button.setOnClickListener {
            val intent = Intent(this,NewMessageActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.logout_menu -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class LatestMessageRow(val chatMessage:ChatMessage) : Item<ViewHolder>()  {
    var partner:User?=null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val partnerId : String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            partnerId = chatMessage.toId
        } else {
            partnerId = chatMessage.fromId!!
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")
        ref.addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                partner = p0.getValue(User::class.java)
                viewHolder.itemView.title.text = partner?.name
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        viewHolder.itemView.description.text = chatMessage.text
    }
}
