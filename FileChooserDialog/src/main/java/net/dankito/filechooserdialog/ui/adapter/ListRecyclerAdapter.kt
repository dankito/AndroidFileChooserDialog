package net.dankito.filechooserdialog.ui.adapter

import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.view.*


abstract class ListRecyclerAdapter<T, THolder : RecyclerView.ViewHolder>(list: List<T> = ArrayList<T>()) : RecyclerView.Adapter<THolder>() {

    var itemClickListener: ((item: T) -> Unit)? = null

    var itemLongClickListener: ((item: T) -> Unit)? = null

    var swipeLayoutOpenedListener: ((THolder) -> Unit)? = null


    abstract protected fun getListItemLayoutId(): Int

    abstract protected fun createViewHolder(itemView: View): THolder

    abstract protected fun bindItemToView(viewHolder: THolder, item: T)


    protected val createdViewHolders = HashSet<THolder>()


    var items: List<T> = list
        set(value) {
            field = value

            notifyDataSetChanged()

            itemsHaveBeenSet(value)
        }

    protected open fun itemsHaveBeenSet(value: List<T>) { }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = position.toLong()

    fun getItem(position: Int) = items[position]


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): THolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(getListItemLayoutId(), parent, false)

        val viewHolder = createViewHolder(itemView)

        viewHolderCreated(viewHolder)

        return viewHolder
    }

    protected open fun viewHolderCreated(viewHolder: THolder) {
        createdViewHolders.add(viewHolder)
    }

    override fun onBindViewHolder(viewHolder: THolder, position: Int) {
        val item = getItem(position)

        if(item == null) {
            bindViewForNullValue(viewHolder)
        }
        else {
            bindViewForNonNullValue(viewHolder, item, position)
        }
    }

    protected open fun bindViewForNullValue(viewHolder: THolder) {
        viewHolder.itemView.visibility = View.GONE
    }

    protected open fun bindViewForNonNullValue(viewHolder: THolder, item: T, position: Int) {
        viewHolder.itemView.visibility = View.VISIBLE

        viewHolder.itemView.isSelected = false // reset selection state
        viewHolder.itemView.isPressed = false

        bindItemToView(viewHolder, item)

        itemBound(viewHolder, item, position)
    }

    protected open fun itemBound(viewHolder: RecyclerView.ViewHolder, item: T, position: Int) {
        viewHolder.itemView.isLongClickable = true // otherwise context menu won't trigger / pop up

        if(itemClickListener != null || itemLongClickListener != null) { // use a GestureDetector as item clickListener also triggers when swiping or long pressing an item
            val gestureDetector = GestureDetectorCompat(viewHolder.itemView.context, TapGestureDetector<T>(item, { itemClicked(viewHolder, item, position) },
                    { itemLongClicked(viewHolder, item, position) }))

            viewHolder.itemView.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        }
        else {
            viewHolder.itemView.setOnTouchListener(null)
        }
    }


    protected open fun itemClicked(viewHolder: RecyclerView.ViewHolder, item: T, position: Int): Boolean {
        itemClickListener?.let {
            it.invoke(item)

            notifyItemChanged(position)
            return true
        }

        return false
    }

    protected open fun itemLongClicked(viewHolder: RecyclerView.ViewHolder, item: T, position: Int) {
        itemLongClickListener?.invoke(item)
    }


    class TapGestureDetector<T>(private val item: T, private val itemClickListener: (item: T) -> Boolean, private val itemLongClickListener: (item: T) -> Unit) : GestureDetector.OnGestureListener {

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return itemClickListener.invoke(item)
        }

        override fun onLongPress(e: MotionEvent?) {
            itemLongClickListener.invoke(item)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent?) { }
    }

}