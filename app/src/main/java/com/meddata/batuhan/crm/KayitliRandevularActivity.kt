package com.meddata.batuhan.crm

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.meddata.batuhan.crm.adapters.AramalarAdapter
import com.meddata.batuhan.crm.adapters.RandevularAdapter
import com.meddata.batuhan.crm.classes.Randevular
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.data.RandevularCevap
import com.meddata.batuhan.crm.databinding.ActivityKayitliRandevularBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KayitliRandevularActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKayitliRandevularBinding
    private lateinit var hdi: HastalarDAOInterface
    private lateinit var randevularListe: List<Randevular>
    private lateinit var adapter: RandevularAdapter

    private var baslangicTarihi: String = ""
    private var bitisTarihi: String = ""
    private var bolum: Int = 0
    private var doktorId: Int = 0
    private var selectedBolumAdi = ""
    private var selectedDoktorAdi = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKayitliRandevularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        }

        binding.toolbarRandevular.title = "Randevu Listesi"
        setSupportActionBar(binding.toolbarRandevular)

        hdi = ApiUtils.getHastalarDAOInterface(this)

        bolum = intent.getIntExtra("bolum", 0)
        doktorId = intent.getIntExtra("doktor", 0)
        val baslangicTarihiString = intent.getStringExtra("baslangic_tarihi") ?: ""
        val bitisTarihiString = intent.getStringExtra("bitis_tarihi") ?: ""
        selectedBolumAdi = intent.getStringExtra("bolum_adi") ?: ""
        selectedDoktorAdi = intent.getStringExtra("doktor_adi") ?: ""

//        Log.e("veri", bolum.toString())
//        Log.e("veri", doktorId.toString())

        binding.rvRandevular.setHasFixedSize(true)
        binding.rvRandevular.layoutManager = LinearLayoutManager(this)

        tumRandevular(baslangicTarihiString, bitisTarihiString, bolum, doktorId)

        binding.fabYenileRandevu.setOnClickListener{

            tumRandevular(baslangicTarihiString, bitisTarihiString, bolum, doktorId)

        }

    }

    fun tumRandevular(baslangic: String, bitis: String, bolum: Int, drKod: Int){

        hdi.tumRandevular(baslangic, bitis, bolum, drKod).enqueue(object: Callback<RandevularCevap>{
            override fun onResponse(call: Call<RandevularCevap>,response: Response<RandevularCevap>) {

                if (response.isSuccessful) {
                    val bodyString = response.body()?.let { Gson().toJson(it) } ?: "Boş yanıt"
                    Log.d("API Yanıtı", bodyString)

                    val liste = response.body()?.randevular
                    if (liste != null && liste.isNotEmpty()) {
                        if (!::adapter.isInitialized) {
                            adapter = RandevularAdapter(this@KayitliRandevularActivity, liste, selectedBolumAdi, selectedDoktorAdi)
                            binding.rvRandevular.adapter = adapter
                        } else {
                            adapter.randevularListe = liste
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

            override fun onFailure(call: Call<RandevularCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }


        })

    }
}