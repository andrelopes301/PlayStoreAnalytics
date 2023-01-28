package com.ipv.playstoreanalytics.utils


import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


open class Popover : BaseBottomSheetDialogFragment() {

    var actionBarHeight = 70

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {

            val tv = TypedValue()
            if (requireActivity().theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            }

            setOnShowListener {
                (this@Popover.dialog as BottomSheetDialog).behavior.apply {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                    this.state = BottomSheetBehavior.STATE_EXPANDED
                    this.peekHeight =  0
                    this.skipCollapsed = true
                    this.isFitToContents = true
                    this.expandedOffset = actionBarHeight.toPX


                    this.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if(newState == BottomSheetBehavior.STATE_HIDDEN){
                                requireView().closeKeyboardForce()
                                dismiss()
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        }
                    })
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 70.toPX
    }

    override fun onDestroyView() {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        super.onDestroyView()
    }




}