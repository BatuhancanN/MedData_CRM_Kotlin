package com.meddata.batuhan.crm

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.meddata.batuhan.crm.adapters.AramalarAdapter
import com.meddata.batuhan.crm.adapters.HastalarAdapter
import com.meddata.batuhan.crm.classes.Aramalar
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.AramalarCevap
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.databinding.ActivityKayitliAramalarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.Duration

class KayitliAramalarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKayitliAramalarBinding
    private lateinit var hdi: HastalarDAOInterface
    private lateinit var adapter: AramalarAdapter
    private lateinit var aramalarListe: ArrayList<Aramalar>

    private var baslangicTarihi: String = ""
    private var bitisTarihi: String = ""
    private var aramaSayisi: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKayitliAramalarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAramalar.title = "Arama Listesi"
        setSupportActionBar(binding.toolbarAramalar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        }

        binding.rvAramalar.setHasFixedSize(true)
        binding.rvAramalar.layoutManager = LinearLayoutManager(this)



        hdi = ApiUtils.getHastalarDAOInterface(this)

        baslangicTarihi = intent.getStringExtra("baslangic_tarihi") ?: ""
        bitisTarihi = intent.getStringExtra("bitis_tarihi") ?: ""
        aramaSayisi = intent.getIntExtra("kacinci_arama", 0)

        tumAramalar(baslangicTarihi, bitisTarihi, aramaSayisi)

        binding.fabYenileArama.setOnClickListener{

            tumAramalar(baslangicTarihi, bitisTarihi, aramaSayisi)

        }


    }

    fun tumAramalar(baslangic: String, bitis: String, arama: Int) {
        hdi.tumAramalar(baslangic, bitis, arama).enqueue(object : Callback<AramalarCevap> {
            override fun onResponse(call: Call<AramalarCevap>, response: Response<AramalarCevap>) {
                if (response.isSuccessful) {
                    // Yanıtı JSON olarak loglayalım
                    val bodyString = response.body()?.let { Gson().toJson(it) } ?: "Boş yanıt"
                    Log.d("API Yanıtı", bodyString)

                    val liste = response.body()?.aramalar
                    if (liste != null && liste.isNotEmpty()) {
                        if (!::adapter.isInitialized) {
                            adapter = AramalarAdapter(this@KayitliAramalarActivity, liste)
                            binding.rvAramalar.adapter = adapter
                        } else {
                            adapter.aramalarListe = liste
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("Hata", "Veri bulunamadı.")
                        Snackbar.make(binding.root, "Veri bulunamadı.", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("Hata", "Response başarısız: ${response.code()}")
                    Snackbar.make(binding.root, "Sunucu hatası: ${response.code()}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AramalarCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }

}