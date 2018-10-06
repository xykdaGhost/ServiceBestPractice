package com.example.yikai.servicebestpractice

import android.graphics.drawable.Icon
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.FieldPosition

class DownloadAdapter(downloadList: List<DownloadTask>) : RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    var downloadList: List<DownloadTask> ?= null

    init {
        this.downloadList = downloadList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.download_item, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return downloadList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val download = downloadList?.get(position)
        holder.downloadName?.setText(download?.getName())
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var downloadName: TextView? = null


        init {
            downloadName = view.findViewById(R.id.download_name) as TextView
            val startIcon = view.findViewById(R.id.start) as ImageView
            val stopIcon = view.findViewById(R.id.stop) as ImageView
            val cancelIcon = view.findViewById(R.id.cancel) as ImageView

            startIcon.setImageIcon(R.drawable.start as Icon)
            stopIcon.setImageIcon(R.drawable.stop as Icon)
            cancelIcon.setImageIcon(R.drawable.cancel as Icon)
        }

    }
}
