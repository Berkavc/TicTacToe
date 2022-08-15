package com.example.common.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.common.R
import com.example.common.dialog.ErrorDialogActivity
import com.example.domain.model.ResultData

abstract class BaseFragment<T : BaseViewModel, B : ViewDataBinding> : Fragment() {
    abstract val layoutRes: Int
    abstract val viewModel: T

    open fun initBinding() {}

    abstract fun observeViewModel()
    abstract fun viewCreated(view: View, savedInstanceState: Bundle?)

    open fun arrangeUI() {}

    open fun gatherArgs() {}

    private var _binding: B? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        initBinding()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gatherArgs()
        viewCreated(view, savedInstanceState)
        arrangeUI()
        observeLoadingAndError()
        observeViewModel()
    }

    private fun observeLoadingAndError() {
        viewModel.loadingErrorState.observe(viewLifecycleOwner) {
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
                else -> {
                    hideLoading()
                }
            }
        }
    }

    private val loadingAlertDialog by lazy {
        context?.let {
            Dialog(it).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setContentView(R.layout.dialog_loading)
                setCancelable(false)
            }
        }
    }

    private fun showLoading() {
        loadingAlertDialog?.show()
    }

    private fun hideLoading() {
        loadingAlertDialog?.dismiss()
    }

    fun showErrorDialog() {
        this.let {
            activity?.let {
                val intent = Intent(it, ErrorDialogActivity::class.java)
                startActivity(intent)
                it.overridePendingTransition(0,0)
            }

        }
    }


    fun navigateToNextFragment(action: NavDirections) {
        view?.let { view ->
//            viewModelStore.clear()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun navigateToNextFragmentWithoutClear(action: NavDirections) {
        view?.let { view ->
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun navigateToUpFragment() {
        view?.let {
            Navigation.findNavController(it).navigateUp()
        }
    }

    fun navigateToNextActivity(intent: Intent) {
        activity?.finish()
        startActivity(intent)
    }

    fun navigateToWithoutPopNextActivity(intent: Intent) {
        startActivity(intent)
        activity?.let {
            it.overridePendingTransition(0,0)
        }
    }

    fun customNavHostBackPress(fragment: Fragment, navHostId: Int) {
        fragment.activity?.let {
            val controller = Navigation.findNavController(it, navHostId)
            controller.let { navController ->
                navController.popBackStack()
            }
        }
    }

    fun finishFragment() {
//        parentFragmentManager.beginTransaction().remove(parentFragmentManager.fragments[0]).commit()
//        parentFragmentManager.popBackStack()
        findNavController().popBackStack()
//        findNavController().popBackStack(parentFragmentManager.fragments[0].id, true)
    }

}
