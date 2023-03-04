package br.com.ijbuenoapks.minhalistadecompras.services

import android.view.View

interface RecyclerViewClickListener {
    fun recyclerViewListClicked(v: View?, position: Int)

}