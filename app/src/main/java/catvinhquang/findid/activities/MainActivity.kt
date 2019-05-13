package catvinhquang.findid.activities

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import catvinhquang.findid.R
import catvinhquang.findid.ViewInfo
import catvinhquang.findid.services.MyAccessibilityService
import catvinhquang.findid.services.MyTileService

/**
 * Created by QuangCV on 30-Apr-2019
 **/

class MainActivity : Activity() {

    companion object {
        private const val KEY_PACKAGE_NAME = "KEY_PACKAGE_NAME"
        private const val KEY_LIST_OF_VIEW = "KEY_LIST_OF_VIEW"
        private const val MAX_VISIBLE_ITEM = 8

        fun navigate(c: Context, packageName: String, listOfViewInfo: ArrayList<ViewInfo>) {
            val intent = Intent(c, MainActivity::class.java)
            intent.putExtra(KEY_PACKAGE_NAME, packageName)
            intent.putExtra(KEY_LIST_OF_VIEW, listOfViewInfo)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            c.startActivity(intent)
        }
    }

    private var title: TextView? = null
    private var list: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val root = findViewById<View>(R.id.root)
        root.layoutParams.width = resources.displayMetrics.widthPixels * 3 / 4
        root.requestLayout()

        title = findViewById(R.id.title)
        title?.movementMethod = ScrollingMovementMethod()

        list = findViewById(R.id.list)
        list?.layoutManager = LinearLayoutManager(this)
        list?.isNestedScrollingEnabled = true
        list?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                when (parent.getChildAdapterPosition(view)) {
                    0 -> outRect.set(0, dpToPx(0), 0, dpToPx(5))
                    state.itemCount - 1 -> outRect.set(0, dpToPx(5), 0, 0)
                    else -> outRect.set(0, dpToPx(5), 0, dpToPx(5))
                }
            }
        })

        render(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        if (intent != null) {
            render(intent)
        }
    }

    private fun render(intent: Intent) {
        val data = intent.getSerializableExtra(KEY_LIST_OF_VIEW) as ArrayList<ViewInfo>
        val adapter = Adapter(data)

        title?.text = intent.getStringExtra(KEY_PACKAGE_NAME)
        title?.setOnClickListener {
            adapter.showId = !adapter.showId
            adapter.notifyDataSetChanged()
        }

        list?.adapter = adapter
        if (data.size > MAX_VISIBLE_ITEM) {
            list?.layoutParams!!.height = (MAX_VISIBLE_ITEM * dpToPx(10) + (MAX_VISIBLE_ITEM + 0.5f) * dpToPx(32)).toInt()
            list?.requestLayout()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MyTileService.isActive) {
            MyAccessibilityService.setActive(true)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    class Adapter(private var data: ArrayList<ViewInfo>) : RecyclerView.Adapter<ViewHolder>() {

        var showId = true

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val holder = ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item,
                    parent,
                    false
                )
            )
            holder.info.movementMethod = ScrollingMovementMethod()

            val l = View.OnLongClickListener {
                val s = getSystemService(parent.context, ClipboardManager::class.java)
                if (s != null) {
                    val itemData = data[holder.adapterPosition]
                    val copiedData = if (showId) itemData.className else itemData.id
                    s.primaryClip = ClipData.newPlainText(null, copiedData)
                    Toast.makeText(parent.context, R.string.copied, Toast.LENGTH_SHORT).show()
                    true
                } else {
                    false
                }
            }
            holder.info.setOnLongClickListener(l)
            holder.itemView.setOnLongClickListener(l)

            return holder
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.number.setBackgroundResource(if (position == 0) R.drawable.bg_current_number else R.drawable.bg_number)
            holder.number.text = (data.size - position).toString()
            updateView(holder.info, data[position])
        }

        private fun updateView(t: TextView, itemData: ViewInfo) {
            t.text = if (showId) itemData.id else itemData.className
            t.scrollTo(0, 0)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val number: TextView = itemView.findViewById(R.id.number)
        val info: TextView = itemView.findViewById(R.id.info)

    }

}
