package com.example.yikai.servicebestpractice

import android.os.AsyncTask
import android.os.Environment
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

class DownloadTask(listener: DownloadListener) : AsyncTask<String, Int, Int>() {

    val TYPE_SUCCESS = 0
    val TYPE_FAILED = 1
    val TYPE_PAUSED = 2
    val TYPE_CANCELED = 3
    val ZERO: Long = 0

    val listener: DownloadListener?= null
    var isCanceled = false
    var isPaused = false
    var lastProgress: Int = 0
    var fileName: String = ""


    override fun doInBackground(vararg params: String?): Int {
        var inputstream: InputStream ?= null
        var savedFile: RandomAccessFile ?= null
        var file: File ?= null
        try {
            var downloadedLength: Long? = 0
            var downloadUrl = params[0]
            fileName = downloadUrl?.substring(downloadUrl.lastIndexOf("/")).toString()
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
            file = File(directory + fileName)
            if (file.exists()) {
                downloadedLength = file.length()
            }
            val contentLength = getContentLength(downloadUrl)
            when (contentLength) {
                ZERO -> TYPE_FAILED
                downloadedLength -> TYPE_SUCCESS
            }
            val client = OkHttpClient()
            val request = Request.Builder().addHeader("RANGE", "bytes=" + downloadedLength + "-").url(downloadUrl).build()
            val response = client.newCall(request).execute()

            if (response != null) {
                inputstream = response.body()?.byteStream()
                savedFile = RandomAccessFile(file, "rw")
                savedFile.seek(downloadedLength!!)
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
                        val progress = ((total + downloadedLength!!) * 100 / contentLength!!) as Int
                        publishProgress(progress)
                    }
                    len = inputstream?.read(b)
                } while (len != -1)
                response.body()?.close()
                return TYPE_SUCCESS
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (inputstream != null) {
                    inputstream.close()
                }
                if (savedFile != null) {
                    savedFile.close()
                }
                if (isCanceled && file != null) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return TYPE_FAILED
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        val progress = values[0]
        if (progress!! > lastProgress) {
            listener?.onProgress(progress)
            lastProgress = progress
        }
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        when (result) {
            TYPE_SUCCESS -> listener?.onSuccess()
            TYPE_FAILED -> listener?.onFailed()
            TYPE_CANCELED -> listener?.onCanceled()
            TYPE_PAUSED -> listener?.onPaused()
        }
    }

    fun pauseDownload() = {isPaused = true}

    fun cancelDownload() = {isCanceled = true}

    fun getContentLength(downloadUrl: String?): Long? {
        val client = OkHttpClient()
        val request = Request.Builder().url(downloadUrl).build()
        val response = client.newCall(request).execute()
        if (response != null && response.isSuccessful) {
            val contentLength = response.body()?.contentLength()
            response.body()?.close()
            return contentLength
        }
        return 0
    }

    fun getName(): String {
        return fileName
    }
}