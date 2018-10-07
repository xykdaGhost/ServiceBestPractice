package com.example.yikai.servicebestpractice

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.util.jar.Manifest

//class MainActivity : AppCompatActivity() {
//
//    var downloadList: List<DownloadTask> = ArrayList<DownloadTask>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)
//        val layoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = layoutManager
//        val adapter = DownloadAdapter(downloadList)
//        recyclerView.adapter = adapter
//    }
//}

class MainActivity : AppCompatActivity(), View.OnClickListener  {

    var downloadBinder: DownloadService.DownloadBinder? = null

    val connection = object : ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBinder = service as DownloadService.DownloadBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    override fun onClick(v: View) {
        if (downloadBinder == null) {
            return;
        }
        when (v.id) {
            R.id.start_download -> {
                val url = "ttps://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe"
                downloadBinder?.startDownload(url)
            }
            R.id.pause_download -> downloadBinder?.pauseDownload()
            R.id.cancel_download -> downloadBinder?.cancelDownload()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val startDownload = findViewById<Button>(R.id.start_download)
        val pauseDownload = findViewById<Button>(R.id.pause_download)
        val cancelDownload = findViewById<Button>(R.id.cancel_download)

        startDownload.setOnClickListener(this)
        pauseDownload.setOnClickListener(this)
        cancelDownload.setOnClickListener(this)

        val intent = Intent(this, DownloadService::class.java)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast("权限不足")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    fun toast(msg: CharSequence) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
