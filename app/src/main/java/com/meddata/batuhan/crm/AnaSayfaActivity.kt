package com.meddata.batuhan.crm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.meddata.batuhan.crm.adapters.AramalarAdapter
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.data.SayisalVeriCevap
import com.meddata.batuhan.crm.databinding.ActivityAnaSayfaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnaSayfaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnaSayfaBinding
    private lateinit var hdi: HastalarDAOInterface
    private var secim: Int = 0

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            veriGetir()
            handler.postDelayed(this, 5000) // 5000 ms = 5 saniye
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAnaSayfaBinding.inflate(layoutInflater)
        setContentView(binding.root)



        hdi = ApiUtils.getHastalarDAOInterface(this)

        val sp = getSharedPreferences("MedData", Context.MODE_PRIVATE)


        veriGetir()
        handler.postDelayed(runnable, 5000) // 5 saniye sonra tekrar çalıştır


        binding.btnHastaKayit.setOnClickListener{
            secim = 1

            val intent = Intent(this@AnaSayfaActivity, TarihSecimiActivity::class.java)
            intent.putExtra("secim", secim)
            startActivity(intent)

        }

        binding.btnAramalar.setOnClickListener{

            secim = 2

            val intent = Intent(this@AnaSayfaActivity, TarihSecimiActivity::class.java)
            intent.putExtra("secim", secim)
            startActivity(intent)


        }

        binding.btnRandevular.setOnClickListener{

            secim = 3

            val intent = Intent(this@AnaSayfaActivity, TarihSecimiActivity::class.java)
            intent.putExtra("secim", secim)
            startActivity(intent)

        }

        binding.btnCikis.setOnClickListener{

            val editor = sp.edit()
            editor.remove("username")
            editor.remove("password")
            editor.remove("lastLoginTime")
            editor.commit()

            startActivity(Intent(this@AnaSayfaActivity, MainActivity::class.java))
            finish()

        }


    }

    fun veriGetir(){

        Log.e("dongu", "DOngu calisti!!")

        hdi.hastaSayisi().enqueue(object : Callback<SayisalVeriCevap> {
            override fun onResponse(
                call: Call<SayisalVeriCevap>,
                response: Response<SayisalVeriCevap>
            ) {
                if (response.isSuccessful) {
                    val sayisalVeriCevap = response.body()
                    Log.d("API Yanıtı", response.body().toString())
                    if (sayisalVeriCevap != null && sayisalVeriCevap.success == 1) {
                        val totalRecords = sayisalVeriCevap.totalRecords
                        val totalCalls = sayisalVeriCevap.totalCalls
                        binding.textBugNGelenHastaSayisi.text = "$totalRecords"
                        binding.textBugNYapilanAramaSayisi.text = "$totalCalls"
                    } else {
                        Log.e("Hata", "Veri bulunamadı.")
                        Snackbar.make(binding.root, "Veri bulunamadı.", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("Hata", "Response başarısız: ${response.code()}")
                    Snackbar.make(binding.root, "Sunucu hatası: ${response.code()}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SayisalVeriCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }
        })

    }
}