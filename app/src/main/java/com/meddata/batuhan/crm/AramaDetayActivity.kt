package com.meddata.batuhan.crm

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.meddata.batuhan.crm.classes.Aramalar
import com.meddata.batuhan.crm.databinding.ActivityAramaDetayBinding
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class AramaDetayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAramaDetayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAramaDetayBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val arama = intent.getSerializableExtra("nesne") as? Aramalar



        arama?.let {

            binding.textSiraNoAD.text = arama.sira_no.toString() ?: ""
            binding.textDosyaNoAD.text = arama.dosya_no.toString() ?: ""
            binding.textProtokolNoAD.text = arama.protokol_no.toString() ?: ""
            binding.textHastaAdisoyadiAD.text = arama.hasta_adsoyad ?: ""
            binding.textBaslangicTarihiAD.text = arama.baslangic_tarihi ?: ""
            binding.textBitisTarihiAD.text = arama.bitis_tarihi ?: ""
            binding.textAramaSayisiAD.text = arama.kacinci_arama.toString() ?: ""
            binding.textAramaNotuAD.text = arama.arama_notu ?: ""

            binding.textAramaSuresiAD.text = "${aramaSuresiHesaplama(arama.baslangic_tarihi, arama.bitis_tarihi)} dk" ?: ""

        }

    }

    fun aramaSuresiHesaplama(baslangic: String, bitis: String) : String {

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val baslangicTarihi = LocalDateTime.parse(baslangic, formatter)
        val bitisTarihi = LocalDateTime.parse(bitis, formatter)
        val sure = Duration.between(baslangicTarihi, bitisTarihi)
        val sureDakika = sure.toMinutes()

        return sureDakika.toString()

        Log.e("zaman", sure.toString())
    }
}