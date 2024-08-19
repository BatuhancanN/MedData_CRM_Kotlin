package com.meddata.batuhan.crm

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meddata.batuhan.crm.adapters.HastalarAdapter
import com.meddata.batuhan.crm.classes.Hastalar
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarCevap
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.databinding.ActivityKayitliHastalarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KayitliHastalarActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var binding: ActivityKayitliHastalarBinding
    private lateinit var hastalarListe: ArrayList<Hastalar>
    private lateinit var adapter: HastalarAdapter
    private lateinit var hdi: HastalarDAOInterface

    private var baslangicTarihi: String = ""
    private var bitisTarihi: String = ""

    private var currentPage: Int = 1
    private val pageSize: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKayitliHastalarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        }

        binding.toolbarHastalar.title = "Hasta Listesi"
        setSupportActionBar(binding.toolbarHastalar)

        binding.rvHastalar.setHasFixedSize(true)
        binding.rvHastalar.layoutManager = LinearLayoutManager(this)

        hdi = ApiUtils.getHastalarDAOInterface(this)

        baslangicTarihi = intent.getStringExtra("baslangic_tarihi") ?: ""
        bitisTarihi = intent.getStringExtra("bitis_tarihi") ?: ""

        if (baslangicTarihi.isNotEmpty() && bitisTarihi.isNotEmpty()) {
            tumHastalar(baslangicTarihi, bitisTarihi, currentPage, pageSize)
        } else {
            Log.e("Tarih Hatası", "Başlangıç veya bitiş tarihi eksik.")
        }

        binding.fabYenile.setOnClickListener {
            tumHastalar(baslangicTarihi, bitisTarihi, currentPage, pageSize)
        }


        binding.fabOnceki.setOnClickListener{

            if (currentPage > 1) {
                currentPage--
                tumHastalar(baslangicTarihi, bitisTarihi, currentPage, pageSize)
            }

        }

        binding.fabSonraki.setOnClickListener{

            currentPage++
            tumHastalar(baslangicTarihi, bitisTarihi, currentPage, pageSize)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_search, menu)

        val item = menu?.findItem(R.id.action_ara)
        val searchView = item?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            if (baslangicTarihi.isNotEmpty() && bitisTarihi.isNotEmpty()) {
                aramaYap(newText, baslangicTarihi, bitisTarihi)
            } else {
                Log.e("Tarih Hatası", "Başlangıç veya bitiş tarihi eksik.")
            }
        }
        return true
    }

    fun tumHastalar(baslangic: String, bitis: String, page: Int, pageSize: Int) {
        hdi.tarihleTumHastalar(baslangic, bitis, page, pageSize).enqueue(object : Callback<HastalarCevap> {
            override fun onResponse(call: Call<HastalarCevap>, response: Response<HastalarCevap>) {
                if (response.isSuccessful) {
                    val cevap = response.body()
                    val liste = cevap?.hastalar
                    val toplamKayit = cevap?.total_records ?: 0
                    var toplamSayfa = Math.ceil(toplamKayit.toDouble() / pageSize).toInt()

                    Log.d("Yanıt", response.body().toString())

                    if (toplamSayfa < 1) toplamSayfa = 1

                    if (liste != null) {
                        if (!::adapter.isInitialized) {
                            adapter = HastalarAdapter(this@KayitliHastalarActivity, liste)
                            binding.rvHastalar.adapter = adapter
                        } else {
                            adapter.hastalarListe = liste
                            adapter.notifyDataSetChanged()
                        }

                        binding.fabSonraki.isEnabled = currentPage < toplamSayfa
                        binding.fabOnceki.isEnabled = currentPage > 1
                    } else {
                        Log.e("Hata", "Veri bulunamadı.")
                    }
                } else {
                    Log.e("Hata", "Sunucu hatası: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<HastalarCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
            }
        })
    }

    fun aramaYap(aranan: String, baslangic: String, bitis: String) {
        hdi.hastaAra(aranan, baslangic, bitis).enqueue(object : Callback<HastalarCevap>{
            override fun onResponse(call: Call<HastalarCevap>, response: Response<HastalarCevap>) {
                if (response.isSuccessful) {
                    val cevap = response.body()
                    val liste = cevap?.hastalar

                    if (liste != null) {
                        adapter = HastalarAdapter(this@KayitliHastalarActivity, liste)
                        binding.rvHastalar.adapter = adapter

                    } else {
                        Log.e("Hata", "Veri bulunamadı.")
                    }
                } else {
                    Log.e("Hata", "Sunucu hatası: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<HastalarCevap>, t: Throwable) {
                Log.e("Hata", "Arama başarısız: ${t.message}")
                Snackbar.make(binding.root, "Arama başarısız: ${t.message}", Snackbar.LENGTH_LONG).show()
            }


        })
    }
}
