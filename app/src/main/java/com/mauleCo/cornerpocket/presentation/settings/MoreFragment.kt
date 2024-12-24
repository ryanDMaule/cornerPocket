package com.mauleCo.cornerpocket.presentation.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mauleCo.cornerpocket.R
import com.mauleCo.cornerpocket.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentMoreBinding? = null
    private val binding get() = _binding!!
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.donationsConstraint.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_donationsFragment)
        }

        binding.privacyConstraint.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/corner-pocket-privacy-policy/home"))
            startActivity(i)
        }

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName, 0
            )
            val version = pInfo.versionName
//            val verCode = pInfo.versionCode

            binding.versionCode.text = version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

//        binding.versionCode.text = BuildConfig

    }

}