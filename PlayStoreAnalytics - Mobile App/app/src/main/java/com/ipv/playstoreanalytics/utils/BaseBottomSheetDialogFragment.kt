package com.ipv.playstoreanalytics.utils;

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils
import com.ipv.playstoreanalytics.MainActivity
import com.ipv.playstoreanalytics.R


abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment()  {

        val activity get() = context as MainActivity
        val component get() = activity.component


        override fun getTheme(): Int {
                return R.style.BottomSheetDialog
        }

        fun show(parentFragmentManager: FragmentManager) {
                show(parentFragmentManager, parentFragmentManager.fragments.javaClass.name ?: "BaseBottomSheetDialogFragment")
        }

        class ElevationOverlayLinearLayout(context: Context, attributes: AttributeSet) : LinearLayout(context, attributes) {
                init {
                        background = MaterialShapeDrawable.createWithElevationOverlay(context, elevation)
                }
                override fun setElevation(elevation: Float) {
                        super.setElevation(elevation)
                        MaterialShapeUtils.setElevation(this, elevation)
                }
        }
}