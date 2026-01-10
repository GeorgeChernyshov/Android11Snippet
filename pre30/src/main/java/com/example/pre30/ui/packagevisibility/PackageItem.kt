package com.example.pre30.ui.packagevisibility

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.pre30.R
import com.example.pre30.databinding.ItemPackageBinding

class PackageItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var binding: ItemPackageBinding

    init {
        if (isInEditMode)
            inflate(context, R.layout.item_package, this)
        else
            binding = ItemPackageBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setData(data: String?) {
        binding.packageNameTextView.text = data
    }
}