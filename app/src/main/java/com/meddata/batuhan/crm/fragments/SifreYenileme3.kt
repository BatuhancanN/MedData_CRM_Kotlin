package com.meddata.batuhan.crm.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.data.SifreDegistirmeCevap
import com.meddata.batuhan.crm.databinding.FragmentSifreYenileme3Binding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class SifreYenileme3 : Fragment() {

    private lateinit var binding: FragmentSifreYenileme3Binding
    private lateinit var hdi: HastalarDAOInterface

    private var gelenLoginame = ""
    private var durum = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = FragmentSifreYenileme3Binding.inflate(inflater, container, false)

        hdi = ApiUtils.getHastalarDAOInterface(requireContext())

        val bundle: SifreYenileme3Args by navArgs()

        gelenLoginame = bundle.loginame

        binding.btnDegistirSY.setOnClickListener{

            if(binding.txtYeniSifreSY.text.isNullOrEmpty() || binding.txtYeniSifreTekrarSY.text.isNullOrEmpty()){

                Snackbar.make(binding.root, "Lütfen tüm alanları doldurun", Snackbar.LENGTH_SHORT).show()

            }
            else{

                if (binding.txtYeniSifreSY.text.toString() == binding.txtYeniSifreTekrarSY.text.toString()){

                    val sifre = binding.txtYeniSifreSY.text.toString().trim()

//                    if (sifreGuvenliMi(sifre)){
//                        sifreDegistir(gelenLoginame, sifre)
//
//                    }
//                    else{
//
//                        Snackbar.make(binding.root, "Şifre güvenli değil! Şifre en az 8 karakter uzunluğunda, bir büyük harf, bir küçük harf, bir rakam ve bir özel karakter içermelidir.", Snackbar.LENGTH_LONG).show()
//
//                    }

                    sifreDegistir(gelenLoginame, sifre)





                }
                else {
                    Snackbar.make(binding.root, "Şifreler aynı değil!!", Snackbar.LENGTH_SHORT).show()
                }

            }

        }


        return binding.root
    }

    fun sifreDegistir(loginame: String, sifre: String){

        hdi.sifreDegistir(loginame, sifre).enqueue(object : Callback<SifreDegistirmeCevap>{
            override fun onResponse(
                call: Call<SifreDegistirmeCevap>,
                response: Response<SifreDegistirmeCevap>
            ) {

                val durumCH = response.body()?.success

                durumCH?.let {

                    durum = durumCH.toInt()

                    if (durum == 1){

                        Toast.makeText(context, "Şifre değiştirme başarılı", Toast.LENGTH_LONG).show()
                        requireActivity().finish()


                    }
                    else if (durum == 0){
                        Snackbar.make(binding.root, "Şifre değiştirme başarısız", Snackbar.LENGTH_LONG).show()

                    }
                    else{
                        Snackbar.make(binding.root, "Bir hata oluştu", Snackbar.LENGTH_LONG).show()

                    }

                }

            }

            override fun onFailure(call: Call<SifreDegistirmeCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }


        })

    }

    private fun sifreGuvenliMi(sifre: String): Boolean {
        val sifreDeseni = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
        return sifreDeseni.matches(sifre)
    }

}