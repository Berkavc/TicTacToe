package com.example.tictactoe.ui


import android.os.Binder
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.LifecycleOwner
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.common.base.BaseActivity
import com.example.common.utils.clickWithThrottle
import com.example.tictactoe.R
import com.example.tictactoe.databinding.ActivityMainBinding
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main
    override val viewModel: MainViewModel by viewModels()
    override var viewLifeCycleOwner: LifecycleOwner = this

    private val layoutList:MutableList<FrameLayout> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun observeViewModel() {}

    override fun arrangeUI() {
        arrangeLayoutList()
        binding.imageViewMainReset.clickWithThrottle {
            resetBoardState()
        }

        binding.frameLayoutMainOne.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainOne, 1)
        }

        binding.frameLayoutMainTwo.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainTwo, 2)
        }

        binding.frameLayoutMainThree.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainThree, 3)
        }

        binding.frameLayoutMainFour.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainFour, 4)
        }

        binding.frameLayoutMainFive.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainFive, 5)
        }

        binding.frameLayoutMainSix.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainSix, 6)
        }

        binding.frameLayoutMainSeven.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainSeven, 7)
        }

        binding.frameLayoutMainEight.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainEight, 8)
        }

        binding.frameLayoutMainNine.clickWithThrottle {
            arrangeClickState(binding.frameLayoutMainNine, 9)
        }
    }

    private fun arrangeClickState(frameLayout: FrameLayout, position: Int) {
        var controlWinner = false
        layoutList.remove(frameLayout)
        frameLayout.isClickable = false
        viewModel.mapOfBoard[position] = viewModel.playerState
        if (controlBoardState()) {
            controlWinner = true
            lockBoard()
            binding.textViewMainWinner.text = resources.getString(R.string.winner , viewModel.playerState.toString())
        }else{
            if(!viewModel.mapOfBoard.containsValue(null)){
                lockBoard()
                binding.textViewMainWinner.text = resources.getString(R.string.draw)
            }
        }
        if (viewModel.playerState == PlayerState.X) {
            frameLayout.background =
                ContextCompat.getDrawable(this, R.drawable.bg_cross)
            viewModel.playerState = PlayerState.O
            if(!controlWinner && layoutList.isNotEmpty()){
                //Bot playing
                aiPlay()
            }
        } else {
            frameLayout.background =
                ContextCompat.getDrawable(this, R.drawable.bg_circle)
            viewModel.playerState = PlayerState.X
        }
    }

    private fun controlBoardState(): Boolean {
        if (viewModel.mapOfBoard[1] != null && viewModel.mapOfBoard[2] != null && viewModel.mapOfBoard[3] != null && viewModel.mapOfBoard[1] == viewModel.mapOfBoard[2] && viewModel.mapOfBoard[2] == viewModel.mapOfBoard[3]) {
            binding.viewMainHorizontalOneWinner.visibility = View.VISIBLE
            return true
        } else if (viewModel.mapOfBoard[4] != null && viewModel.mapOfBoard[5] != null && viewModel.mapOfBoard[6] != null && viewModel.mapOfBoard[4] == viewModel.mapOfBoard[5] && viewModel.mapOfBoard[5] == viewModel.mapOfBoard[6]) {
            binding.viewMainHorizontalTwoWinner.visibility = View.VISIBLE
                return true
        } else if (viewModel.mapOfBoard[7] != null && viewModel.mapOfBoard[8] != null && viewModel.mapOfBoard[9] != null && viewModel.mapOfBoard[7] == viewModel.mapOfBoard[8] && viewModel.mapOfBoard[8] == viewModel.mapOfBoard[9]) {
            binding.viewMainHorizontalThreeWinner.visibility = View.VISIBLE
            return true
        } else if (viewModel.mapOfBoard[1] != null && viewModel.mapOfBoard[4] != null && viewModel.mapOfBoard[7] != null && viewModel.mapOfBoard[1] == viewModel.mapOfBoard[4] && viewModel.mapOfBoard[4] == viewModel.mapOfBoard[7]) {
            binding.viewMainVerticalOneWinner.visibility = View.VISIBLE
            return true
        } else if (viewModel.mapOfBoard[2] != null && viewModel.mapOfBoard[5] != null && viewModel.mapOfBoard[8] != null && viewModel.mapOfBoard[2] == viewModel.mapOfBoard[5] && viewModel.mapOfBoard[5] == viewModel.mapOfBoard[8]) {
            binding.viewMainVerticalTwoWinner.visibility = View.VISIBLE
            return true
        } else if (viewModel.mapOfBoard[3] != null && viewModel.mapOfBoard[6] != null && viewModel.mapOfBoard[9] != null && viewModel.mapOfBoard[3] == viewModel.mapOfBoard[6] && viewModel.mapOfBoard[6] == viewModel.mapOfBoard[9]) {
            binding.viewMainVerticalThreeWinner.visibility = View.VISIBLE
            return true
        } else if (viewModel.mapOfBoard[1] != null && viewModel.mapOfBoard[5] != null && viewModel.mapOfBoard[9] != null && viewModel.mapOfBoard[1] == viewModel.mapOfBoard[5] && viewModel.mapOfBoard[5] == viewModel.mapOfBoard[9]) {
            binding.viewMainAxisTwoWinner.visibility = View.VISIBLE
            return true
        } else if (viewModel.mapOfBoard[3] != null && viewModel.mapOfBoard[5] != null && viewModel.mapOfBoard[7] != null && viewModel.mapOfBoard[3] == viewModel.mapOfBoard[5] && viewModel.mapOfBoard[5] == viewModel.mapOfBoard[7]) {
            binding.viewMainAxisOneWinner.visibility = View.VISIBLE
            return true
        }
        return false
    }

    private fun resetBoardState() {
        viewModel.resetBoardState()
        resetBoardComponent(binding.frameLayoutMainOne)
        resetBoardComponent(binding.frameLayoutMainTwo)
        resetBoardComponent(binding.frameLayoutMainThree)
        resetBoardComponent(binding.frameLayoutMainFour)
        resetBoardComponent(binding.frameLayoutMainFive)
        resetBoardComponent(binding.frameLayoutMainSix)
        resetBoardComponent(binding.frameLayoutMainSeven)
        resetBoardComponent(binding.frameLayoutMainEight)
        resetBoardComponent(binding.frameLayoutMainNine)
        arrangeLayoutList()
    }

    private fun resetBoardComponent(frameLayout: FrameLayout) {
        frameLayout.background = null
        frameLayout.isClickable = true
        resetBoardWinnerComponent()
    }

    private fun resetBoardWinnerComponent(){
        binding.viewMainVerticalOneWinner.visibility = View.GONE
        binding.viewMainVerticalTwoWinner.visibility = View.GONE
        binding.viewMainVerticalThreeWinner.visibility = View.GONE
        binding.viewMainHorizontalOneWinner.visibility = View.GONE
        binding.viewMainHorizontalTwoWinner.visibility = View.GONE
        binding.viewMainHorizontalThreeWinner.visibility = View.GONE
        binding.viewMainAxisOneWinner.visibility = View.GONE
        binding.viewMainAxisTwoWinner.visibility = View.GONE
        binding.textViewMainWinner.text = null
    }

    private fun lockBoard(){
        binding.frameLayoutMainOne.isClickable = false
        binding.frameLayoutMainTwo.isClickable = false
        binding.frameLayoutMainThree.isClickable = false
        binding.frameLayoutMainFour.isClickable = false
        binding.frameLayoutMainFive.isClickable = false
        binding.frameLayoutMainSix.isClickable = false
        binding.frameLayoutMainSeven.isClickable = false
        binding.frameLayoutMainEight.isClickable = false
        binding.frameLayoutMainNine.isClickable = false
    }

    private fun arrangeLayoutList(){
        layoutList.clear()
        layoutList.add(binding.frameLayoutMainOne)
        layoutList.add(binding.frameLayoutMainTwo)
        layoutList.add(binding.frameLayoutMainThree)
        layoutList.add(binding.frameLayoutMainFour)
        layoutList.add(binding.frameLayoutMainFive)
        layoutList.add(binding.frameLayoutMainSix)
        layoutList.add(binding.frameLayoutMainSeven)
        layoutList.add(binding.frameLayoutMainEight)
        layoutList.add(binding.frameLayoutMainNine)
    }

    private fun aiPlay(){
        layoutList.random().performClick()
    }

}