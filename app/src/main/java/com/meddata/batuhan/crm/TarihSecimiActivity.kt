package com.meddata.batuhan.crm

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.meddata.batuhan.crm.classes.Bolumler
import com.meddata.batuhan.crm.classes.Doktorlar
import com.meddata.batuhan.crm.classes.Randevular
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.BolumlerCevap
import com.meddata.batuhan.crm.data.DoktorlarCevap
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.databinding.ActivityTarihSecimiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TarihSecimiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTarihSecimiBinding
    private lateinit var txtBaslangicTarih: AppCompatButton
    private lateinit var txtBitisTarih: AppCompatButton
    private lateinit var btnGetir: AppCompatButton

    private lateinit var hdi: HastalarDAOInterface
    private lateinit var bolumlerListe: ArrayList<Bolumler>
    private lateinit var doktorlarListe: ArrayList<Doktorlar>

    private lateinit var spinnerBolumler: Spinner
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedBolumId = 0
    private var selectedDoktorId = 0
    private var selectedBolumAdi = ""
    private var selectedDoktorAdi = ""

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Tarih değişkenleri
    private var baslangicTarih: Calendar = Calendar.getInstance()
    private var bitisTarih: Calendar = Calendar.getInstance()
    private var aramaSayisi = 0

    private var secim: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTarihSecimiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hdi = ApiUtils.getHastalarDAOInterface(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        }

        Toast.makeText(this@TarihSecimiActivity, "Lütfen giriş alanlarına çift tıklayınız", Toast.LENGTH_LONG).show()

        binding.toolbarTarihSecimi.title = "Filtreleme Sihirbazı"
        setSupportActionBar(binding.toolbarTarihSecimi)

        secim = intent.getIntExtra("secim", 0)

        txtBaslangicTarih = binding.btnBaslangicTarihiTS
        txtBitisTarih = binding.btnBitisTarihiTS
        btnGetir = binding.btnGetir

        txtBaslangicTarih.setOnClickListener { showDatePickerDialog(true) }
        txtBitisTarih.setOnClickListener { showDatePickerDialog(false) }

        if (secim == 1) {
            binding.txtAramaSayisiTS.isVisible = false
            binding.spBolumler.isVisible = false
            binding.spDoktorlar.isVisible = false
        }

        if (secim == 2) {
            binding.txtAramaSayisiTS.isVisible = true
            binding.spBolumler.isVisible = false
            binding.spDoktorlar.isVisible = false
        }

        if (secim == 3) {
            binding.spBolumler.isVisible = true
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mutableListOf<String>())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBolumler.adapter = adapter

        tumBolumler()

        binding.spBolumler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedBolum = bolumlerListe[position]
                selectedBolumId = selectedBolum.bolum
                selectedBolumAdi = selectedBolum.bolum_adi

                Log.d("Bolum", "ID: $selectedBolumId, Bolum: ${selectedBolum.bolum_adi}")

                adapter = ArrayAdapter(this@TarihSecimiActivity, android.R.layout.simple_spinner_item, mutableListOf<String>())
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spDoktorlar.adapter = adapter


                tumDoktorlar(selectedBolumId)

                binding.spDoktorlar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                        val selectedDoktor = doktorlarListe[position]
                        selectedDoktorId = selectedDoktor.dr_kodu
                        selectedDoktorAdi = selectedDoktor.adi_soyadi

                        if (doktorlarListe.isNullOrEmpty()){
                            binding.spDoktorlar.isVisible = false
                        }
                        else{
                            binding.spDoktorlar.isVisible = true
                        }

                        Log.e("Doktor", "ID: $selectedDoktorId, Doktor: ${selectedDoktor.adi_soyadi}")


                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {

                    }


                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Seçili öğe olmadığında
            }
        }

        btnGetir.setOnClickListener {
            when (secim) {
                1 -> {
                    if (binding.btnBaslangicTarihiTS.text.isNullOrEmpty() || binding.btnBitisTarihiTS.text.isNullOrEmpty()) {
                        Toast.makeText(this@TarihSecimiActivity, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                    } else {
                        val baslangicTarihiString = dateFormat.format(baslangicTarih.time)
                        val bitisTarihiString = dateFormat.format(bitisTarih.time)

                        val intent = Intent(this@TarihSecimiActivity, KayitliHastalarActivity::class.java)
                        intent.putExtra("baslangic_tarihi", baslangicTarihiString)
                        intent.putExtra("bitis_tarihi", bitisTarihiString)
                        startActivity(intent)
                        finish()
                    }
                }
                2 -> {
                    if (binding.btnBaslangicTarihiTS.text.isNullOrEmpty() || binding.btnBitisTarihiTS.text.isNullOrEmpty() || binding.txtAramaSayisiTS.text.isNullOrEmpty()) {
                        Snackbar.make(binding.root, "Lütfen tüm alanları doldurun...", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val baslangicTarihiString = dateTimeFormat.format(baslangicTarih.time)
                        val bitisTarihiString = dateTimeFormat.format(bitisTarih.time)

                        if (binding.txtAramaSayisiTS.text.isDigitsOnly()) {
                            val aramaSayisiStr = binding.txtAramaSayisiTS.text.toString()
                            aramaSayisi = aramaSayisiStr.toInt()
                        }
                        else{
                            Snackbar.make(binding.root, "Lütfen arama sayısı girişinizi kontrol edin!", Snackbar.LENGTH_SHORT)
                        }


                        val intent = Intent(this@TarihSecimiActivity, KayitliAramalarActivity::class.java)
                        intent.putExtra("baslangic_tarihi", baslangicTarihiString)
                        intent.putExtra("bitis_tarihi", bitisTarihiString)
                        intent.putExtra("kacinci_arama", aramaSayisi)
                        startActivity(intent)
                        finish()
                    }
                }
                3 -> {
                    if (binding.btnBaslangicTarihiTS.text.isNullOrEmpty() || binding.btnBitisTarihiTS.text.isNullOrEmpty()) {
                        Toast.makeText(this@TarihSecimiActivity, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                    } else {
                        if (selectedDoktorId != 0){
                            val baslangicTarihiString = dateFormat.format(baslangicTarih.time)
                            val bitisTarihiString = dateFormat.format(bitisTarih.time)

                            val intent = Intent(this@TarihSecimiActivity, KayitliRandevularActivity::class.java)
                            intent.putExtra("baslangic_tarihi", baslangicTarihiString)
                            intent.putExtra("bitis_tarihi", bitisTarihiString)
                            intent.putExtra("bolum", selectedBolumId)
                            intent.putExtra("doktor", selectedDoktorId)
                            intent.putExtra("bolum_adi", selectedBolumAdi)
                            intent.putExtra("doktor_adi", selectedDoktorAdi)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Log.e("Hata", "Lütfen Bölüm Ve Doktor Seçin")
                            Snackbar.make(binding.root, "Lütfen Bölüm Ve Doktor Seçin", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {
                    // Default case
                }
            }
        }
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                if (secim == 2) {
                    showTimePickerDialog(selectedDate, isStartDate)
                } else {
                    val formattedDate = dateFormat.format(selectedDate.time)
                    if (isStartDate) {
                        txtBaslangicTarih.text = formattedDate
                        baslangicTarih = selectedDate // Başlangıç tarihini güncelle
                    } else {
                        txtBitisTarih.text = formattedDate
                        bitisTarih = selectedDate // Bitiş tarihini güncelle
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(selectedDate: Calendar, isStartDate: Boolean) {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedDate.set(Calendar.MINUTE, minute)
                val formattedDate = dateTimeFormat.format(selectedDate.time)
                if (isStartDate) {
                    txtBaslangicTarih.text = formattedDate
                    baslangicTarih = selectedDate // Başlangıç tarihini güncelle
                } else {
                    txtBitisTarih.text = formattedDate
                    bitisTarih = selectedDate // Bitiş tarihini güncelle
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }

    private fun tumBolumler() {
        hdi.tumBolumler().enqueue(object : Callback<BolumlerCevap> {
            override fun onResponse(call: Call<BolumlerCevap>, response: Response<BolumlerCevap>) {
                if (response.isSuccessful) {
                    // Yanıtı JSON olarak loglayalım
                    val bodyString = response.body()?.let { Gson().toJson(it) } ?: "Boş yanıt"
                    Log.d("API Yanıtı", bodyString)

                    val liste = response.body()?.bolumler
                    if (liste != null && liste.isNotEmpty()) {
                        bolumlerListe = ArrayList(liste)
                        val bolumIsimleri = liste.map { it.bolum_adi }
                        adapter.clear()
                        adapter.addAll(bolumIsimleri)
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("Hata", "Bölüm bulunamadı.")
                        Snackbar.make(findViewById(android.R.id.content), "Bölüm bulunamadı.", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("Hata", "Response başarısız: ${response.code()}")
                    Snackbar.make(findViewById(android.R.id.content), "Sunucu hatası: ${response.code()}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BolumlerCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(findViewById(android.R.id.content), "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun tumDoktorlar(bolum: Int) {
        hdi.tumDoktorlar(bolum).enqueue(object : Callback<DoktorlarCevap> {
            override fun onResponse(call: Call<DoktorlarCevap>, response: Response<DoktorlarCevap>) {
                if (response.isSuccessful) {

                    val bodyString = response.body()?.let { Gson().toJson(it) } ?: "Boş yanıt"
                    Log.d("API Yanıtı", bodyString)

                    val liste = response.body()?.doktorlar
                    if (liste != null && liste.isNotEmpty()) {
                        doktorlarListe = ArrayList(liste)
                        val doktorIsimleri = liste.map { it.adi_soyadi }
                        adapter.clear()
                        adapter.addAll(doktorIsimleri)
                        adapter.notifyDataSetChanged()
                    } else {
                        if (selectedBolumId != 4000){
                            Log.e("Hata", "Doktor bulunamadı.")
                            Snackbar.make(findViewById(android.R.id.content), "Doktor bulunamadı.", Snackbar.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e("Hata", "Response başarısız: ${response.code()}")
                    Snackbar.make(findViewById(android.R.id.content), "Sunucu hatası: ${response.code()}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DoktorlarCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(findViewById(android.R.id.content), "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }
}
