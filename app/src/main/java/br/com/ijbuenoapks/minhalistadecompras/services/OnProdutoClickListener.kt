package br.com.ijbuenoapks.minhalistadecompras.services

import br.com.ijbuenoapks.minhalistadecompras.models.Produto

interface OnProdutoClickListener {

    /**
     * Quando o usuario clicar no icone de apagar este meto sera invocado para remoção do item em sua posicao
     */
    fun onDelete(model: Produto)
}