package com.example.yikai.servicebestpractice

import android.os.AsyncTask
import android.os.Environment
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import kotlin.math.E

class DownloadTask(listener: DownloadListener) : AsyncTask<String, Int, Int>() {

    val TYPE_SUCCESS = 0
    val TYPE_FAILED = 1
    val TYPE_PAUSED = 2
    val TYPE_CANCELED = 3

    val listener: DownloadListener ?= null
    val isCanceled = false
    val isPaused = false
    val lastProgress: Int = 0


    override fun doInBackground(vararg params: String?): Int {
        var inputstream: InputStream ?= null
        var savedFile: RandomAccessFile ?= null
        var file: File ?= null
        try {
            var downloadedLength = 0
            var downloadUrl = params[0]
            var fileName = downloadUrl?.substring(downloadUrl.lastIndexOf("/"))
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            file = File(directory + fileName)
            if (file.exists()) {
                downloadedLength = file.length().toInt()
            }
            val contentLength = getContentLength()
            when (contentLength) {
                0 -> TYPE_FAILED
                downloadedLength -> TYPE_SUCCESS
            }
            val client = OkHttpClient()
            val request = Request.Builder().addHeader("RANGE", "bytes=" + downloadedLength + "-").url(downloadUrl).build()
            val response = client.newCall(request).execute()

            if (response != null) {
                inputstream = response.body()?.byteStream()
                savedFile = RandomAccessFile(file, "rw")
                savedFile.seek(downloadedLength.toLong())
                var b = ByteArray(1024)
                var total = 0
                var len = inputstream?.read(b)
                do {
                    if (isCanceled) {
                        return TYPE_CANCELED
                    } else if (isPaused) {
                        return TYPE_PAUSED
                    } else{
                        total += len!!
                        savedFile.write(b, 0, len)
                        val progress = ((total + downloadedLength) * 100 / contentLength) as Int
                        publishProgress(progress)
                    }
                    len = inputstream?.read(b)
                } while (len != -1)

            }


        }
    }

}