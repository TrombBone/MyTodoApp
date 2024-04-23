package com.example.mytodoapp.abstracts

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet() : BottomSheetDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.dialog?.setOnShowListener {
            val bottomSheetDialog = this.dialog as BottomSheetDialog
            with(bottomSheetDialog.behavior) {
                state = BottomSheetBehavior.STATE_EXPANDED
                saveFlags = BottomSheetBehavior.SAVE_ALL
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

}