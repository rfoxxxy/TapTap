package com.kieronquinn.app.taptap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.models.TapActionCategory
import com.kieronquinn.app.taptap.utils.Links
import com.kieronquinn.app.taptap.utils.dip
import kotlinx.android.synthetic.main.fragment_add_action_category.*
import kotlinx.android.synthetic.main.fragment_github_selector.*

class GithubSelectorFragment : Fragment() {

    private val navController by lazy {
        findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_github_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        github_original.setOnClickListener {
            context?.let { it1 -> Links.startLinkIntent(it1, Links.LINK_GITHUB) }
        }
        github_fork.setOnClickListener {
            context?.let { it1 -> Links.startLinkIntent(it1, Links.FORK_GITHUB) }
        }
        view.setOnApplyWindowInsetsListener { v, insets ->
            v.layoutParams.apply {
                this as FrameLayout.LayoutParams
                bottomMargin = insets.systemWindowInsetBottom
            }
            insets
        }
    }

    private fun moveToCategory(category: TapActionCategory){
        if(category == TapActionCategory.ADVANCED){
            navController.navigate(R.id.action_actionCategoryFragment_to_actionCategoryInfoFragment)
        }else {
            val bundle = Bundle()
            bundle.putSerializable("category", category)
            navController.navigate(R.id.action_actionCategoryFragment_to_actionListFragment, bundle)
        }
    }

}