package com.mymusic.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.databinding.ItemCommentBinding
import com.kt_media.domain.constant.CHILD_ARTIST
import com.kt_media.domain.constant.CHILD_ARTIST_ID
import com.kt_media.domain.constant.CHILD_GENRE
import com.kt_media.domain.constant.CHILD_GENRE_ID
import com.kt_media.domain.constant.CHILD_ID
import com.kt_media.domain.constant.CHILD_SONG
import com.kt_media.domain.constant.CHILD_USER
import com.kt_media.domain.entities.Comment
import com.kt_media.domain.entities.Song
import com.kt_media.domain.entities.User


class CommentAdapter : RecyclerView.Adapter<CommentViewHolder>() {

    private val list: ArrayList<Comment> = arrayListOf()

    fun submit(list: List<Comment>) {

        this.list.clear()
        this.list.addAll(list)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {

        val binding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = list[position]
        setViewUser(holder,item.userId)
        holder.binding.tvContentSi.text = item.content
    }

    private fun setViewUser(holder:CommentViewHolder,userId:String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(CHILD_USER)
        val query =
            databaseReference.orderByChild(CHILD_ID).equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    val user = data.getValue(User::class.java)
                    if(user!=null){
                        Glide.with(holder.binding.cirIvAvatarCi).load(user.image)
                            .into(holder.binding.cirIvAvatarCi)
                        holder.binding.tvNameCi.text=user.name
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}

class CommentViewHolder(val binding: ItemCommentBinding) :
    RecyclerView.ViewHolder(binding.root)


