package br.com.ijbuenoapks.minhalistadecompras.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.ijbuenoapks.minhalistadecompras.R
import br.com.ijbuenoapks.minhalistadecompras.models.ProdutoLista

class AdapterProduto(
    private val context : Context,
    private val produtoLista: MutableList<ProdutoLista>
    ) : RecyclerView.Adapter<AdapterProduto.ProdutoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val itemLista = LayoutInflater.from(context)
            .inflate(
                R.layout.itens_da_lista,
                parent,
                false
            )
        return ProdutoViewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        holder.nome.text = produtoLista[position].produto.produto
        holder.quantidade.text = produtoLista[position].quantidade.toString()
        holder.valor.text = produtoLista[position].produto.valor.toString()
        holder.botaoLixeira.setImageResource(produtoLista[position].lixeira)
    }

    override fun getItemCount(): Int = produtoLista.size


        inner class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val nome = itemView.findViewById<TextView>(R.id.txtNomeProduto)
            val quantidade = itemView.findViewById<TextView>(R.id.txtQtd)
            val valor = itemView.findViewById<TextView>(R.id.txtValor)
            val botaoLixeira = itemView.findViewById<ImageButton>(R.id.botaoLixeira)
        }


}