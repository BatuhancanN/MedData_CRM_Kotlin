package com.meddata.batuhan.crm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.meddata.batuhan.crm.classes.Hastalar
import com.meddata.batuhan.crm.databinding.ActivityHastaDetayBinding


class HastaDetayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHastaDetayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHastaDetayBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val hasta = intent.getSerializableExtra("nesne") as? Hastalar

        hasta?.let {

            binding.textDosyaNoHD.text = hasta.dosya_no.toString() ?: ""
            binding.textProtokolNoHD.text = hasta.protokol_no.toString() ?: ""
            binding.textHastaAdisoyadiHD.text = hasta.hasta_adsoyad ?: ""
            binding.textTakipNotuHD.text = hasta.takip_notu ?: ""
            binding.textHastaTelNoHD.text = hasta.tel_no ?: ""
            binding.textHastaTcHD.text = hasta.tc_kimlik_no ?: ""
            binding.textTarihHD.text = hasta.tarih ?: ""
            binding.textSorumluPersonelHD.text = hasta.sorumlu_personel ?: ""
            binding.textKullaniciHD.text = hasta.kullanici_acan ?: ""
            binding.textDuzenlenenKayitHD.text = hasta.duzenlenen_kayit ?: ""
            binding.textSiraNoHD.text = hasta.sira_no.toString() ?: ""
            binding.textHastaneNoHD.text = hasta.hastane_no.toString() ?: ""
            binding.textTakipTuruHD.text = hasta.takip_turu.toString() ?: ""
            binding.textDurumuHD.text = hasta.durumu.toString() ?: ""
            binding.textRandevuHD.text = hasta.randevu ?: ""
            binding.textTakipAciklamasiHD.text = hasta.takip_aciklamasi ?: ""
        }

    }
}