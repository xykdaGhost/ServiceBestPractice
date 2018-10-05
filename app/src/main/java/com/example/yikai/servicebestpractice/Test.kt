package com.example.yikai.servicebestpractice

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class Test {

    internal var downloadUrl = IntArray(10)
    internal var client = OkHttpClient()
    internal var request = Request()
    internal var response = client.newCall(request).execute()
    internal var b = ByteArray(1024)
}
