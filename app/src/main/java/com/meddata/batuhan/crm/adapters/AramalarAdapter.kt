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
import com.meddata.batuhan.crm.classes.Aramalar

class AramalarAdapter(private val mContext: Context, var aramalarListe: List<Aramalar>): RecyclerView.Adapter<AramalarAdapter.CardTasarimTutucu>() {

    inner class CardTasarimTutucu(tasarim: View) : RecyclerView.ViewHolder(tasarim) {
        var arama_card: CardView = tasarim.findViewById(R.id.card_arama)
        var textAramaTarih: TextView = tasarim.findViewById(R.id.textAramaTarihAramaCard)
        var textAramaSayisi: TextView = tasarim.findViewById(R.id.textAramaSayisiAramaCard)
        var textSiraNo: TextView = tasarim.findViewById(R.id.textSiraNoAramaCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.card_tasarim_arama, parent, false)
        return CardTasarimTutucu(tasarim)
    }

    override fun getItemCount(): Int {
        return aramalarListe.size
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val arama = aramalarListe[position]
        holder.textAramaSayisi.text = arama.kacinci_arama.toString()
        holder.textAramaTarih.text = arama.baslangic_tarihi
        holder.textSiraNo.text = arama.sira_no.toString()

        holder.arama_card.setOnClickListener {
            val intent = Intent(mContext, AramaDetayActivity::class.java)
            intent.putExtra("nesne", arama)
            mContext.startActivity(intent)
        }

        holder.arama_card.setOnLongClickListener {
            Toast.makeText(mContext, arama.arama_notu, Toast.LENGTH_LONG).show()
            true
        }
    }
}
