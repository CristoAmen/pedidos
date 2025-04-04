package com.example.pedidos

import Pedido
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class PedidoAdapter(
    private val listaPedidos: MutableList<Pedido>,
    private val onClick: (Pedido, Int) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        val txtMonto: TextView = itemView.findViewById(R.id.txtMonto)
        // Agrega otros botones o vistas si es necesario.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.txtCliente.text = "Cliente: ${pedido.nombreCliente}"
        holder.txtDescripcion.text = "Descripción: ${pedido.descripcion}"
        holder.txtDireccion.text = "Dirección: ${pedido.direccion}"
        holder.txtMonto.text = "Monto: $${pedido.monto}"

        holder.itemView.setOnClickListener {
            onClick(pedido, position)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size
}
