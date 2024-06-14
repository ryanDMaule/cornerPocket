package com.example.cornerpocket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentUserDetailsBinding
import com.example.cornerpocket.viewModels.MainViewModel

class user_details : Fragment() {
    private var _binding : FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        val user = viewModel.getUser()
        Log.i("UDF", "user: $user")

        if (user != null){
            binding.userNameTv.text = user.name
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_user_details_to_playFragment)
        }

        binding.applyBtn.setOnClickListener {
            val updatedName = binding.inputTextName.text.toString()
            if (updatedName.isNotBlank()){
                viewModel.updateUser(updatedName)
            } else {
                Toast.makeText(requireActivity(), "TEXT FIELD CANNOT BE BLANK", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}