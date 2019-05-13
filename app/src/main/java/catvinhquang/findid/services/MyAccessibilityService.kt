package catvinhquang.findid.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo.*
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import catvinhquang.findid.ViewInfo
import catvinhquang.findid.activities.MainActivity

/**
 * Created by QuangCV on 30-Apr-2019
 **/

class MyAccessibilityService : AccessibilityService() {

    companion object {
        private var instance: MyAccessibilityService? = null
        fun setActive(v: Boolean) {
            val info = instance?.serviceInfo
            if (v) {
                info?.eventTypes = AccessibilityEvent.TYPE_VIEW_HOVER_EXIT
                info?.feedbackType = FEEDBACK_ALL_MASK
                info?.flags = DEFAULT or FLAG_REPORT_VIEW_IDS or FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or FLAG_REQUEST_TOUCH_EXPLORATION_MODE
            } else {
                info?.eventTypes = 0
                info?.feedbackType = 0
                info?.flags = 0
            }
            instance?.serviceInfo = info
        }
    }

    override fun onServiceConnected() {
        instance = this
        Toast.makeText(this, "onServiceConnected", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("MyAccessibilityService", event?.toString() + "\n\n" + event?.source?.toString())

        val packageName = event?.source?.packageName
        val prefixId = packageName?.toString() + ":id/"

        var list = ArrayList<ViewInfo>()
        var view = event?.source
        var id: String?
        while (view != null) {
            id = view.viewIdResourceName
            id = if (TextUtils.isEmpty(id)) "NO ID" else id.replace(prefixId, "")
            list.add(ViewInfo(id, view.className.toString()))
            view = view.parent
        }

        if (!TextUtils.isEmpty(packageName) && list.size > 0) {
            MainActivity.navigate(this, packageName.toString(), list)
            setActive(false)
        }
    }

    override fun onInterrupt() {
    }

}