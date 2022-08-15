package com.example.common.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.example.common.R
import com.example.common.dialog.ErrorDialogActivity
import com.example.domain.model.ResultData

abstract class BaseActivity<T : BaseViewModel, B : ViewDataBinding> : AppCompatActivity() {
    abstract val layoutRes: Int
    abstract val viewModel: T

    abstract var viewLifeCycleOwner: LifecycleOwner

    open fun arrangeUI() {}

    open fun gatherArgs() {}

    open fun initBinding() {
        this._binding?.lifecycleOwner = this
        viewLifeCycleOwner = this
    }

    abstract fun observeViewModel()


    private var _binding: B? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)
        _binding = DataBindingUtil.inflate(layoutInflater, layoutRes, null, false)
        setContentView(_binding!!.root)
        gatherArgs()
        initBinding()
        arrangeUI()
        observeLoadingAndError()
        observeViewModel()
    }


    override fun onDestroy() {
        super.onDestroy()
//        _binding = null
    }

    private fun observeLoadingAndError() {
        viewModel.loadingErrorState.observe(viewLifeCycleOwner) {
            when (it) {
                is ResultData.Loading -> {
                    showLoading()
                }
                is ResultData.Success -> {
                    hideLoading()
                }
                is ResultData.Failed -> {
                    hideLoading()
                    showErrorDialog()
                }
            }
        }

    }

    private val loadingAlertDialog by lazy {
        this.let {
            Dialog(it).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(R.layout.dialog_loading)
                setCancelable(false)
            }
        }
    }

    private fun showLoading() {
        loadingAlertDialog.show()
    }

    private fun hideLoading() {
        loadingAlertDialog.dismiss()
    }

    fun showErrorDialog() {
        this.let {
            val intent = Intent(this, ErrorDialogActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
        }
    }


    fun navigateToNextActivity(activity: Activity, intent: Intent) {
        activity.finish()
        startActivity(intent)
        overridePendingTransition(0,0)
    }

    fun navigateToWithoutPopNextActivity(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(0,0)
    }

    fun finishActivityWithoutAnimation(){
        finish()
        overridePendingTransition(0,0)
    }

    fun resetActivity(activity: Activity){
        val intent = Intent(activity , activity.javaClass)
        activity.finish()
        startActivity(intent)
        overridePendingTransition(0,0)
    }


}
