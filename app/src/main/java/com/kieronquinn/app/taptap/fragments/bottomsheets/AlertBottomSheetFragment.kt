package com.kieronquinn.app.taptap.fragments.bottomsheets

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.kieronquinn.app.taptap.R
import kotlinx.android.synthetic.main.bottom_sheet_buttons.*
import kotlinx.android.synthetic.main.fragment_bottomsheet_generic.*

class AlertBottomSheetFragment : BottomSheetFragment() {

    companion object {
        const val KEY_MESSAGE = "message"
        const val KEY_TITLE = "title"
        const val KEY_POSITIVE_LABEL = "positive_label"
        const val KEY_NEGATIVE_LABEL = "negative_label"
        var ok_listener: (() -> (Boolean))? = {true}
        var negative_listener: (() -> (Boolean))? = {true}

        fun create(message: CharSequence, @StringRes title: Int, @StringRes positiveLabel: Int, @StringRes negativeLabel: Int?, okListen: (() -> (Boolean))?, negativeListener: (() -> (Boolean))?): AlertBottomSheetFragment {
            ok_listener = okListen
            negative_listener = negativeListener
            val arguments = bundleOf(KEY_MESSAGE to message, KEY_TITLE to title, KEY_POSITIVE_LABEL to positiveLabel, KEY_NEGATIVE_LABEL to negativeLabel)
            return AlertBottomSheetFragment().apply {
                this.arguments = arguments
            }
        }
    }

    private var savedData: Bundle? = null

    init {
        layout = R.layout.fragment_bottomsheet_generic
        okListener = ok_listener
        isCancelable = true
        isSwipeable = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments ?: return
        savedData = bundle
        okLabel = bundle.getInt(KEY_POSITIVE_LABEL)
        val negative_label = bundle.getInt(KEY_NEGATIVE_LABEL)
        cancelLabel = negative_label
        cancelListener = negative_listener
        val title = bundle.getInt(KEY_TITLE)
        val message = bundle.getCharSequence(KEY_MESSAGE, "")
        super.onViewCreated(view, savedInstanceState)
        text.text = message
        bs_toolbar_title.text = getString(title)
        bottom_sheet_ok.text = getString(okLabel!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(savedData)
    }

}