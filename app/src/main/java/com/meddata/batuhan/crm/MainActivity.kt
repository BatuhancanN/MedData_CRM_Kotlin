package com.meddata.batuhan.crm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.meddata.batuhan.crm.data.ApiUtils
import com.meddata.batuhan.crm.data.HastalarDAOInterface
import com.meddata.batuhan.crm.data.LoginCevap
import com.meddata.batuhan.crm.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var hdi: HastalarDAOInterface

    private var server = ""

    private var username = ""
    private var password = ""
    private lateinit var sp: SharedPreferences
    private lateinit var sp2: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hdi = ApiUtils.getHastalarDAOInterface(this)

        sp = getSharedPreferences("MedData", Context.MODE_PRIVATE)

        sp2 = getSharedPreferences("MedDataServerCon", Context.MODE_PRIVATE)






        val alUs = sp.getString("username", "")
        val alPas = sp.getString("password", "")
        val lastLoginTime = sp.getLong("lastLoginTime", 0L)

        if (alUs != null && alPas != null && lastLoginTime != 0L) {
            val currentTime = System.currentTimeMillis()
            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
            if (currentTime - lastLoginTime < twentyFourHoursInMillis) {
                autoLogin(alUs, alPas)
            }
        }

        val fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val bottom_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        binding.topLinearLayout.animation = bottom_down

        val handler = Handler()
        val runnable = Runnable {
            binding.textView43.animation = fade_in
            binding.cardView2.animation = fade_in
            binding.cardView.animation = fade_in
        }

        handler.postDelayed(runnable, 1000)

        binding.btnServer.setOnClickListener{

            val intent = Intent(this@MainActivity, ServerAyarlamaActivity::class.java)
            startActivity(intent)

        }

        binding.btnGiris.setOnClickListener {



            if (binding.txtUsername.text.isNullOrEmpty() || binding.txtPassword.text.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Lütfen tüm alanları doldurun.", Snackbar.LENGTH_SHORT).show()
            } else {
                username = binding.txtUsername.text.toString()
                password = binding.txtPassword.text.toString()

                Toast.makeText(this, "Bağlantı Kuruluyor...", Toast.LENGTH_SHORT).show()

                loginKontrol(username, password)
            }
        }

        binding.bcLogoImage.setOnClickListener {
            val webpage: Uri = Uri.parse("https://google.com")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Log.e("MainActivity", "No application can handle this request. Please install a web browser.")
            }
        }

        binding.textSifremiUnuttum.setOnClickListener{

            val intent = Intent(this@MainActivity, SifreYenilemeActivity::class.java)
            startActivity(intent)

        }
    }

    fun serverBaglanti() : String {

        server = sp2.getString("server", "") ?: ""
        return server

    }

    fun loginKontrol(username: String, password: String) {
        hdi.login(username, password).enqueue(object : Callback<LoginCevap> {
            override fun onResponse(call: Call<LoginCevap>, response: Response<LoginCevap>) {
                if (response.isSuccessful) {
                    val bodyString = response.body()?.let { Gson().toJson(it) } ?: "Boş yanıt"
                    Log.d("API Yanıtı", bodyString)

                    val yetki = response.body()?.yetki
                    val sonuc = response.body()?.logged
                    if (sonuc != 0) {
                        if (yetki == 1) {
                            val editor = sp.edit()
                            editor.putString("username", username)
                            editor.putString("password", password)
                            editor.putLong("lastLoginTime", System.currentTimeMillis())
                            editor.commit()

                            val intent = Intent(this@MainActivity, AnaSayfaActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Snackbar.make(binding.root, "Kullanıcının CRM yetkisi yok.", Snackbar.LENGTH_LONG).show()
                            Log.e("Hata", "Kullanıcının CRM yetkisi yok.")
                        }
                    } else {
                        Snackbar.make(binding.root, "Bir hata oluştu...", Snackbar.LENGTH_LONG).show()
                        Log.e("Hata", "Bir hata oluştu...")
                    }
                } else {
                    Log.e("Hata", "Response başarısız: ${response.code()}")
                    Snackbar.make(binding.root, "Sunucu hatası: ${response.code()}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    fun autoLogin(username: String, password: String) {
        hdi.login(username, password).enqueue(object : Callback<LoginCevap> {
            override fun onResponse(call: Call<LoginCevap>, response: Response<LoginCevap>) {
                if (response.isSuccessful) {
                    val bodyString = response.body()?.let { Gson().toJson(it) } ?: "Boş yanıt"
                    Log.d("API Yanıtı", bodyString)

                    val yetki = response.body()?.yetki
                    val sonuc = response.body()?.logged
                    if (sonuc != 0) {
                        if (yetki == 1) {
                            val intent = Intent(this@MainActivity, AnaSayfaActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Snackbar.make(binding.root, "Kullanıcının CRM yetkisi yok.", Snackbar.LENGTH_LONG).show()
                            Log.e("Hata", "Kullanıcının CRM yetkisi yok.")
                        }
                    } else {
                        Snackbar.make(binding.root, "Bir hata oluştu...", Snackbar.LENGTH_LONG).show()
                        Log.e("Hata", "Bir hata oluştu...")
                    }
                } else {
                    Log.e("Hata", "Response başarısız: ${response.code()}")
                    Snackbar.make(binding.root, "Sunucu hatası: ${response.code()}", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginCevap>, t: Throwable) {
                Log.e("Hata", t.message.toString())
                Snackbar.make(binding.root, "Bağlantı hatası: ${t.message}", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        hdi = ApiUtils.getHastalarDAOInterface(this)
    }
}
