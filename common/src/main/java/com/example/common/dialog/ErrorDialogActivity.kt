package com.example.common.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import com.example.common.R
import com.example.common.base.BaseActivity
import com.example.common.databinding.ActivityErrorDialogBinding
import com.example.common.utils.clickWithThrottle

class ErrorDialogActivity : BaseActivity<ErrorDialogViewModel, ActivityErrorDialogBinding>() {
    override val viewModel: ErrorDialogViewModel by viewModels()
    override val layoutRes: Int = R.layout.activity_error_dialog
    override var viewLifeCycleOwner: LifecycleOwner = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun arrangeUI() {
        binding.constraintLayoutErrorDialog.clickWithThrottle {
            finishActivityWithoutAnimation()
        }
    }

    override fun observeViewModel() {}

}
