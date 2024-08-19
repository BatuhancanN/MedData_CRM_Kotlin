package com.meddata.batuhan.crm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.meddata.batuhan.crm.AramaDetayActivity
import com.meddata.batuhan.crm.R
import com.meddata.batuhan.crm.RandevuDetayActivity
import com.meddata.batuhan.crm.classes.Randevular

class RandevularAdapter (private val mContext: Context, var randevularListe: List<Randevular>, private val selectedBolumAdi: String, private val selectedDoktorAdi: String): RecyclerView.Adapter<RandevularAdapter.CardTasarimTutucu>() {

    inner class CardTasarimTutucu(tasarim: View) : RecyclerView.ViewHolder(tasarim){

            var randevu_card: CardView
            var textSiraNoRC: TextView
            var textTarihRC: TextView
            var textSaatRC: TextView

            init{

                randevu_card = tasarim.findViewById(R.id.randevu_card)
                textSiraNoRC = tasarim.findViewById(R.id.textSiraNoRC)
                textTarihRC = tasarim.findViewById(R.id.textTarihRC)
                textSaatRC = tasarim.findViewById(R.id.textSaatRC)

            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.card_tasarim_randevu, parent, false)
        return CardTasarimTutucu(tasarim)
    }

    override fun getItemCount(): Int {
        return randevularListe.size
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {


        val randevu = randevularListe[position]
        holder.textSiraNoRC.text = randevu.rnd_sira_no.toString()
        holder.textTarihRC.text = randevu.tarih
        holder.textSaatRC.text = randevu.rnd_saat

        holder.randevu_card.setOnClickListener{

            val intent = Intent(mContext, RandevuDetayActivity::class.java)
            intent.putExtra("nesne", randevu)
            intent.putExtra("doktor_adi", selectedDoktorAdi)
            intent.putExtra("bolum_adi", selectedBolumAdi)
            mContext.startActivity(intent)

        }

        holder.randevu_card.setOnLongClickListener{

            Toast.makeText(mContext, "${randevu.adi} ${randevu.soyadi}", Toast.LENGTH_SHORT).show()
            true

        }

    }

}