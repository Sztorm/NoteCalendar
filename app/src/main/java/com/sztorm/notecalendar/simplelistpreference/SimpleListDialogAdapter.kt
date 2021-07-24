package com.sztorm.notecalendar.simplelistpreference

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.databinding.FragmentSimpleListDialogItemBinding

class SimpleListDialogAdapter(context: Context, entries: Array<CharSequence>, checkedPosition: Int = -1) :
    ArrayAdapter<CharSequence>(context, R.layout.fragment_simple_list_dialog_item, entries) {
    private var checkedItemPosition: Int = checkedPosition
    private var checkedRadio: RadioButton? = null
    var onItemClickListener: ((item: CharSequence?, position: Int) -> Unit)? = null
    var onCreateViewHolderListener: ((holder: ViewHolder) -> Unit)? = null

    inner class ViewHolder(binding: FragmentSimpleListDialogItemBinding) {
        val itemRadio: RadioButton = binding.radioItem
        val itemLabel: TextView = binding.lblItem
        val itemLayout: LinearLayout = binding.root
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: FragmentSimpleListDialogItemBinding
        val holder: ViewHolder

        if (convertView === null) {
            val inflater = LayoutInflater.from(parent.context)
            binding = FragmentSimpleListDialogItemBinding.inflate(inflater, parent, false)
            holder = ViewHolder(binding)

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

            binding.root.tag = holder
        }
        else {
            binding = FragmentSimpleListDialogItemBinding.bind(convertView)
            holder = binding.root.tag as ViewHolder
        }
        holder.itemLabel.text = getItem(position)

        return binding.root
    }
}