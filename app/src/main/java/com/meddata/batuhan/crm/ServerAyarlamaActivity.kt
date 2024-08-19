package com.meddata.batuhan.crm

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.meddata.batuhan.crm.databinding.ActivityServerAyarlamaBinding
import java.net.HttpURLConnection
import java.net.URL

class ServerAyarlamaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerAyarlamaBinding
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityServerAyarlamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        }

        sp = getSharedPreferences("MedDataServerCon", Context.MODE_PRIVATE)
        val gelenServer = sp.getString("server", "http://10.10.10.10/")
        binding.txtServerIp.setText(gelenServer)

        binding.btnServerKaydet.setOnClickListener {
            if (binding.txtServerIp.text.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Lütfen girişi kontrol edin!", Snackbar.LENGTH_SHORT).show()
            } else {
                val server = binding.txtServerIp.text.toString()

                Snackbar.make(binding.root, "Bağlantı kontrol ediliyor, lütfen bekleyin...", Snackbar.LENGTH_LONG).show()

                ServerConnectionTask(server) { isReachable, message, ping ->
                    if (isReachable) {
                        val editor = sp.edit()
                        editor.putString("server", server)
                        editor.commit()

                        Toast.makeText(this@ServerAyarlamaActivity, "$message (Ping: $ping ms)", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@ServerAyarlamaActivity, "$message (Ping: $ping ms)", Toast.LENGTH_LONG).show()
                    }
                }.execute()
            }
        }
    }

    private class ServerConnectionTask(
        private val server: String,
        private val callback: (Boolean, String, Long) -> Unit
    ) : AsyncTask<Void, Void, Pair<Boolean, String>>() {

        override fun doInBackground(vararg params: Void?): Pair<Boolean, String> {
            return try {
                val url = URL(server)
                val startTime = System.currentTimeMillis() // Başlangıç zamanı

                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.requestMethod = "GET"
                connection.connect()

                val endTime = System.currentTimeMillis() // Bitiş zamanı
                val duration = endTime - startTime // Ping süresi

                if (connection.responseCode == 200) {
                    Pair(true, "Server Erişilebilir. Ping: ${duration}ms")
                } else {
                    Pair(false, "Server Cevabı: ${connection.responseCode}. Ping: ${duration}ms")
                }
            } catch (e: Exception) {
                Pair(false, "Bağlantı Kurulamadı: ${e.message}")
            }
        }

        override fun onPostExecute(result: Pair<Boolean, String>) {
            callback(result.first, result.second, 0)
        }
    }}



// YEDEK KOD - PING TESTİNİ ATLAYAN, SADECE DOMAINI KAYIT ALTINA ALIP, GİRİŞ ESNASINDA BAĞLANTIYI KONTROL EDEN KOD
// HTTPS İLE İLGİLİ PROBLEM ÇIKARSA AŞAĞIDAKİ KOD KULLANILACAK

//package com.meddata.batuhan.crm
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.os.Build
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.google.android.material.snackbar.Snackbar
//import com.meddata.batuhan.crm.databinding.ActivityServerAyarlamaBinding
//
//class ServerAyarlamaActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityServerAyarlamaBinding
//
//    private lateinit var sp: SharedPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityServerAyarlamaBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
//        }
//
//        sp = getSharedPreferences("MedDataServerCon", Context.MODE_PRIVATE)
//
//        val gelenServer = sp.getString("server", "http://10.10.10.10/")
//
//        binding.txtServerIp.setText(gelenServer)
//
//
//        binding.btnServerKaydet.setOnClickListener{
//
//            if(binding.txtServerIp.text.isNullOrEmpty()){
//
//                Snackbar.make(binding.root, "Lütfen girişi kontrol edin!", Snackbar.LENGTH_SHORT).show()
//
//            }
//            else{
//
//                val server = binding.txtServerIp.text.toString()
//
//
//                val editor = sp.edit()
//                editor.putString("server", server)
//                editor.commit()
//
//                finish()
//
//            }
//
//        }
//
//    }
//}