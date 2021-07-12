package com.sztorm.notecalendar.simplelistpreference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sztorm.notecalendar.R

class SimpleListDialogAdapter(context: Context, entries: Array<CharSequence>, checkedPosition: Int = -1) :
    ArrayAdapter<CharSequence>(context, R.layout.fragment_simple_list_dialog_item, entries) {
    private val inflater = context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var checkedItemPosition: Int = checkedPosition
    private var checkedRadio: RadioButton? = null
    var onItemClickListener: ((item: CharSequence?, position: Int) -> Unit)? = null
    var onCreateViewHolderListener: ((holder: ViewHolder) -> Unit)? = null

    inner class ViewHolder(view: View) {
        val itemRadio: RadioButton = view.findViewById(R.id.radioItem)
        val itemLabel: TextView = view.findViewById(R.id.lblItem)
        val itemLayout: LinearLayout = view as LinearLayout
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val resultView: View
        val holder: ViewHolder

        if (convertView === null) {
            resultView = inflater
                .inflate(R.layout.fragment_simple_list_dialog_item, parent, false)
            holder = ViewHolder(resultView)
            val clickListener = View.OnClickListener {
                checkedRadio?.isChecked = false
                checkedItemPosition = position
                checkedRadio = holder.itemRadio

                holder.itemRadio.isChecked = true
                onItemClickListener?.invoke(getItem(position), position)
            }
            holder.itemLayout.setOnClickListener(clickListener)
            holder.itemRadio.setOnClickListener(clickListener)

            if (checkedItemPosition == position) {
                checkedRadio = holder.itemRadio
                holder.itemRadio.isChecked = true
            }
            else {
                holder.itemRadio.isChecked = false
            }
            onCreateViewHolderListener?.invoke(holder)

            resultView.tag = holder
        }
        else {
            resultView = convertView
            holder = resultView.tag as ViewHolder
        }
        holder.itemLabel.text = getItem(position)

        return resultView
    }
}