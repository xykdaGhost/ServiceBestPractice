package com.example.yikai.servicebestpractice

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import java.io.File

class DownloadService : Service() {

    public var downloadTask: DownloadTask? = null
    var downloadUrl: String? = null

    val listener= object : DownloadListener{
        override fun onProgress(progress: Int) {

        }

        override fun onSuccess() {
            downloadTask = null
            stopForeground(true)
            getNotificationManager().notify(1, getNotification("下载成功", -1))
            toast("下载成功")
        }

        override fun onFailed() {
            downloadTask = null
            stopForeground(true)
            getNotificationManager().notify(1, getNotification("下载失败", -1))
            toast("下载失败")
        }

        override fun onPaused() {
            downloadTask = null
            toast("下载暂停")
        }

        override fun onCanceled() {
            downloadTask = null
            stopForeground(true)
            toast("下载取消")
        }
    }

    val binder = DownloadBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class DownloadBinder : Binder() {

        fun startDownload(url: String) {
            if (downloadTask == null) {
                downloadUrl = url
                downloadTask = DownloadTask(listener)
                downloadTask!!.execute(downloadUrl)
                startForeground(1, getNotification("下载中...", 0))
                toast("下载中...")
            }
        }

        fun pauseDownload() {
            if (downloadTask != null) {
                downloadTask?.pauseDownload()
            }
        }

        fun cancelDownload() {
            if (downloadTask != null) {
                downloadTask?.cancelDownload()
            }
            if (downloadUrl != null) {
                val fileName = downloadUrl?.substring(downloadUrl?.lastIndexOf("/")!!)
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                val file = File(directory + fileName)
                if (file.exists()) {
                    file.delete()
                }
                getNotificationManager().cancel(1)
                stopForeground(true)
                toast("下载取消")
            }
        }
    }

    fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getNotification(title: String, progress: Int) : Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channelId = "channel"
            val channelName = "下载通知"
            val importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        val notification = NotificationCompat.Builder(this, "channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .build()
        if (progress >= 0) {
            NotificationCompat.Builder(this, "channel")
                    .setContentText(progress.toString() + "%")
                    .setProgress(100, progress, false)
        }
        return notification
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val channel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun toast(msg: CharSequence) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
