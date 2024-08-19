package com.meddata.batuhan.crm.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.meddata.batuhan.crm.AnaSayfaActivity
import com.meddata.batuhan.crm.adapters.RandevularAdapter
import com.meddata.batuhan.crm.classes.Kimlik
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.data.KimlikCevap
import com.meddata.batuhan.crm.data.KimlikKontrolCevap
import com.meddata.batuhan.crm.data.KullaniciKontrolCevap
import com.meddata.batuhan.crm.databinding.FragmentSifreYenileme2Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class SifreYenileme2 : Fragment() {

    private lateinit var binding: FragmentSifreYenileme2Binding
    private lateinit var hdi: HastalarDAOInterface

    private var gelenLoginame = ""
    private var gelenTc = ""
    private var durum = 0

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = FragmentSifreYenileme2Binding.inflate(inflater, container, false)

        hdi = ApiUtils.getHastalarDAOInterface(requireContext())

        val bundle: SifreYenileme2Args by navArgs()

        gelenTc = bundle.tckn
        gelenLoginame = bundle.loginame

        binding.btnDevam2SY.setOnClickListener{

            if (binding.txtSeriNoSY.text.isNullOrEmpty() || binding.txtDogumYeriSY.text.isNullOrEmpty()){

                Snackbar.make(binding.root, "Lütfen tüm alanları doldurun.", Snackbar.LENGTH_SHORT).show()
            }
            else{

                val seri = binding.txtSeriNoSY.text.toString().trim()
                val dogum = binding.txtDogumYeriSY.text.toString().trim()

                kontrol(gelenTc, seri, dogum, gelenLoginame)

            }

        }


        return binding.root
    }


    fun kontrol(tc: String, seri: String, dogum: String, loginame: String){

        hdi.kimlikKontrol(tc, seri, dogum).enqueue(object : Callback<KimlikKontrolCevap>{
            override fun onResponse(
                call: Call<KimlikKontrolCevap>,
                response: Response<KimlikKontrolCevap>
            ) {

                val durumCH = response.body()?.success

                durumCH?.let {

                    durum = durumCH.toInt()

                    if (durum == 1){
                        Log.e("islem", "islem basarili")

                        val gecis = SifreYenileme2Directions.ucuncuAsamaGecis(loginame)

                        findNavController().navigate(gecis)

                    }
                    else if (durum == 0){
                        Log.e("islem", "islem basarisiz")

                        Snackbar.make(binding.root, "Lütfen bilgilerini kontrol edin.", Snackbar.LENGTH_LONG).show()

                    }

                } ?: 0


            }

            override fun onFailure(call: Call<KimlikKontrolCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }


        })

    }

}