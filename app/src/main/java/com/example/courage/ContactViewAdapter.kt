package com.example.courage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactViewAdapter(private val clist:List<ContactsDetail>):RecyclerView.Adapter<ContactViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewAdapter.ViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.custom_contact_view,parent,false)
        return ViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: ContactViewAdapter.ViewHolder, position: Int) {
        val currentContact=clist[position]
        holder.cname.text=currentContact.name
        holder.cphone.text=currentContact.phone
        holder.caddress.text=currentContact.address
        holder.cemail.text=currentContact.email

    }

    override fun getItemCount(): Int {
        return clist.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val cname : TextView = itemView.findViewById(R.id.contact_name)
        val cphone : TextView = itemView.findViewById(R.id.contact_phone)
        val cemail : TextView = itemView.findViewById(R.id.contact_email)
        val caddress : TextView = itemView.findViewById(R.id.contact_address)

    }
}