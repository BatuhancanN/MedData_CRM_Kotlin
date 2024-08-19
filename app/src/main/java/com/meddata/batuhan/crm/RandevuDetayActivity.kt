package com.meddata.batuhan.crm

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.meddata.batuhan.crm.classes.Randevular
import com.meddata.batuhan.crm.databinding.ActivityRandevuDetayBinding

class RandevuDetayActivity : AppCompatActivity() {

    private var bolumAdi = ""
    private var doktorAdi = ""

    private lateinit var binding: ActivityRandevuDetayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRandevuDetayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        }

        val randevu = intent.getSerializableExtra("nesne") as? Randevular
        bolumAdi = intent.getStringExtra("bolum_adi") ?: ""
        doktorAdi = intent.getStringExtra("doktor_adi") ?: ""

        randevu?.let {

            binding.textSiraNoRD.text = randevu.sira_no.toString() ?: ""
            binding.textBolumAdiRD.text = bolumAdi ?: ""
            binding.textRndSiraNoRD.text = randevu.rnd_sira_no.toString() ?: ""
            binding.textDoktorAdiRD.text = doktorAdi ?: ""
            binding.textHastaAdisoyadiRD.text = "${randevu.adi.toString() ?: ""} ${randevu.soyadi ?: ""}"
            binding.textTarihRD.text = "${randevu.tarih.toString() ?: ""} ${randevu.rnd_saat ?: ""}"
            binding.textTcRD.text = randevu.tc_kimlik_no ?: ""

        }


    }
}