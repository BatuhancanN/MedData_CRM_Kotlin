package com.meddata.batuhan.crm.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.meddata.batuhan.crm.HastaDetayActivity
import com.meddata.batuhan.crm.R
import com.meddata.batuhan.crm.classes.Hastalar

class HastalarAdapter (private val mContext: Context, var hastalarListe: List<Hastalar>): RecyclerView.Adapter<HastalarAdapter.CardTasarimTutucu>() {


    inner class CardTasarimTutucu(tasarim: View) : RecyclerView.ViewHolder(tasarim){

        var hasta_card: CardView
        var textSiraNoHasta: TextView
        var textHastaAdi: TextView

        init{

            hasta_card = tasarim.findViewById(R.id.hasta_card)
            textSiraNoHasta = tasarim.findViewById(R.id.textSiraNoHasta)
            textHastaAdi = tasarim.findViewById(R.id.textHastaAdisoyadi)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {

        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.card_tasarim_hasta, parent, false)
        return CardTasarimTutucu(tasarim)

    }

    override fun getItemCount(): Int {
        return hastalarListe.size
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {

        val hasta = hastalarListe[position]

        holder.textSiraNoHasta.text = hasta.sira_no.toString()
        holder.textHastaAdi.text = hasta.hasta_adsoyad

        holder.hasta_card.setOnClickListener{

            val intent = Intent(mContext, HastaDetayActivity::class.java)

            intent.putExtra("nesne", hasta)
            mContext.startActivity(intent)

        }

    }


}