package br.com.ijbuenoapks.minhalistadecompras.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.ijbuenoapks.minhalistadecompras.R
import br.com.ijbuenoapks.minhalistadecompras.models.Produto
import br.com.ijbuenoapks.minhalistadecompras.models.ProdutoLista
import br.com.ijbuenoapks.minhalistadecompras.services.OnProdutoClickListener


class AdapterProduto(
    private val context : Context,
    private val onProdutoClickListener: OnProdutoClickListener,
    private val produtoLista: MutableList<ProdutoLista>
    ) : RecyclerView.Adapter<AdapterProduto.ProdutoViewHolder>() {



    private lateinit var produtoViewHolder : ProdutoViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val inflater = LayoutInflater.from(context)
        val view =  inflater.inflate(R.layout.itens_da_lista, parent, false)
        val holder = ProdutoViewHolder(view)

        produtoViewHolder = holder

        //item view é a viewroot para localizarção da linha
        holder.itemView.setOnClickListener{
            val posicao = holder.adapterPosition
            val modelo = produtoLista[posicao].produto
        }

        //removo o item quando lico no icone da lixeira
        holder.btnLixeira.setOnClickListener{
            val posicao = holder.adapterPosition
            val modelo = produtoLista[posicao].produto
            onProdutoClickListener.onDelete(modelo)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        try {
            holder.nome.text = produtoLista[position].produto.produto
            holder.quantidade.text = produtoLista[position].quantidade.toString()
            holder.valor.text = produtoLista[position].produto.valor.toString()
            holder.btnLixeira.setImageResource(produtoLista[position].lixeira)
        }catch (e: Exception){
            println("ERRO --> " + e.message)
        }
    }

    override fun getItemCount(): Int = produtoLista.size

    fun removeProduto(modelo : Produto){
        val posicao = produtoViewHolder.adapterPosition
        val modelo = produtoLista[posicao].produto
        onProdutoClickListener.onDelete(modelo)

    }

    inner class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById<TextView>(R.id.txtNomeProdutoLista)
        val quantidade: TextView = itemView.findViewById<TextView>(R.id.txtQnatidadeNaLista)
        val valor: TextView = itemView.findViewById<TextView>(R.id.txtValorNaLista)
        val btnLixeira: ImageButton = itemView.findViewById<ImageButton>(R.id.botaoLixeira)

        }
    }


