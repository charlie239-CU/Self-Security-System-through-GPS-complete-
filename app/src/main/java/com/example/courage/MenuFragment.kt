package com.example.courage
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.viewbinding.ViewBindings


class MenuFragment : Fragment() {
    lateinit var addContact:Button
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addContact=view.findViewById<Button>(R.id.nav_contact_button)
        addContact.setOnClickListener {
            var intent=Intent(view.context,Contact::class.java)
            startActivity(intent)
        }
    }


}