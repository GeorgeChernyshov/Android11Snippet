package com.example.pre30.ui.packagevisibility

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pre30.databinding.FragmentPackageVisibilityBinding

class PackageVisibilityFragment : Fragment() {

    private lateinit var binding: FragmentPackageVisibilityBinding

    private val recyclerAdapter = PackagesRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackageVisibilityBinding.inflate(
            inflater,
            container,
            false
        )

        binding.packagesRecyclerView.adapter = recyclerAdapter

        binding.queryPackagesButton.setOnClickListener {
            val packages = requireContext().packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN),
                PackageManager.MATCH_ALL
            ).map { it.activityInfo.packageName }

            recyclerAdapter.items = packages
        }

        return binding.root
    }
}