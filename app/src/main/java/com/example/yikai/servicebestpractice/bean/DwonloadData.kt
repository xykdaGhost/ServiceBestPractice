package com.example.yikai.servicebestpractice.bean

class DwonloadData{
    var downloadName: String = ""
    var downloadUrl: String = ""
    var downloadSize: Int = 0

    fun getdownloadName() = downloadName
    fun setdownloadName(name: String) = {downloadName = name}

    fun getdownloadUrl() = downloadUrl
    fun setdownloadUrl(url: String) = {downloadUrl = url}

    fun getdownloadSize() = downloadSize
    fun setdownloadSize(size: Int) = {downloadSize = size}

}