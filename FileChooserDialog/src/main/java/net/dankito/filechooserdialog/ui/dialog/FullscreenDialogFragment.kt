package net.dankito.filechooserdialog.ui.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.dankito.filechooserdialog.R


abstract class FullscreenDialogFragment : DialogFragment() {


    abstract fun getDialogTag(): String

    abstract fun getLayoutId(): Int

    abstract fun setupUI(rootView: View)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(getLayoutId(), container, false)

        rootView.setOnTouchListener { _, _ -> true } // prevent that unhandled touches bubble up to activity

        setupToolbar(rootView)

        setupUI(rootView)

        savedInstanceState?.let {
            restoreState(savedInstanceState)
        }

        return rootView
    }

    protected open fun restoreState(savedInstanceState: Bundle) {
        // may be overwritten in sub class
    }

    private fun setupToolbar(rootView: View) {
        (rootView.findViewById(R.id.toolbar) as? Toolbar)?.let { toolbar ->
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_close_clear_cancel)
            toolbar.setNavigationOnClickListener { navigationButtonClicked() }

            customizeToolbar(rootView, toolbar)
        }
    }

    protected open fun customizeToolbar(rootView: View, toolbar: Toolbar) {
        // may be overwritten in sub classes
    }


    protected open fun navigationButtonClicked() {
        closeDialogOnUiThread()
    }

    protected open fun closeDialogOnUiThread() {
        dismiss()
    }


    protected open fun showInFullscreen(fragmentManager: FragmentManager, hideStatusBar: Boolean = false) {
        val style = if(hideStatusBar) R.style.FullscreenDialog else R.style.FullscreenDialogWithStatusBar
        setStyle(DialogFragment.STYLE_NORMAL, style)

        show(fragmentManager, getDialogTag())
    }

}