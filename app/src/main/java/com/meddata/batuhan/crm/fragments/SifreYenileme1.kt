package com.meddata.batuhan.crm.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.data.KullaniciKontrolCevap
import com.meddata.batuhan.crm.databinding.FragmentSifreYenileme1Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SifreYenileme1 : Fragment() {

    private lateinit var binding: FragmentSifreYenileme1Binding
    private lateinit var hdi: HastalarDAOInterface

    private var durum = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSifreYenileme1Binding.inflate(inflater, container, false)


        hdi = ApiUtils.getHastalarDAOInterface(requireContext())

        binding.btnDevamSY.setOnClickListener {

            if (binding.txtLoginameSY.text.isNullOrEmpty() ||
                binding.txtAdiSY.text.isNullOrEmpty() ||
                binding.txtSoyadiSY.text.isNullOrEmpty() ||
                binding.txtTcSY.text.isNullOrEmpty() ||
                binding.txtEmailSY.text.isNullOrEmpty() ||
                binding.txtTelSY.text.isNullOrEmpty()) {

                Snackbar.make(binding.root, "Lütfen tüm alanları doldurun.", Snackbar.LENGTH_SHORT).show()

            } else {

                val loginame = binding.txtLoginameSY.text.toString().trim()
                val adi = binding.txtAdiSY.text.toString().trim()
                val soyadi = binding.txtSoyadiSY.text.toString().trim()
                val tckn = binding.txtTcSY.text.toString().trim()
                val email = binding.txtEmailSY.text.toString().trim()
                val telefon = binding.txtTelSY.text.toString().trim()

                kontrol(loginame, adi, soyadi, tckn, email, telefon)

            }
        }

        return binding.root
    }

    fun kontrol(loginame: String, ad: String, soyad: String, tc: String, email: String, telefon: String) {

        hdi.kullaniciKontrol(loginame, ad, soyad, tc, email, telefon).enqueue(object:
            Callback<KullaniciKontrolCevap> {
            override fun onResponse(
                call: Call<KullaniciKontrolCevap>,
                response: Response<KullaniciKontrolCevap>
            ) {


                val durumCH = response.body()?.success
                if (durumCH != null) {

                    durum = durumCH.toInt()

                    if (durum == 1){
                        Log.e("islem", "islem basarili")

                        val gecis = SifreYenileme1Directions.ikinciAsamaGecis(tc, loginame)

                        findNavController().navigate(gecis)

                    }
                    else if (durum == 0){
                        Log.e("islem", "islem basarisiz")

                        Snackbar.make(binding.root, "Lütfen bilgilerini kontrol edin.", Snackbar.LENGTH_LONG).show()

                    }

                } else {
                    Log.e("Hata", "Veri bulunamadı.")
                    Snackbar.make(binding.root, "Veri bulunamadı.", Snackbar.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<KullaniciKontrolCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }


        })

    }
}
