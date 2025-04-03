package com.example.pedidos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PedidoAdapter(private val pedidos: List<Pedido>) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clienteTextView: TextView = view.findViewById(R.id.txtCliente)
        val direccionTextView: TextView = view.findViewById(R.id.txtDireccion)
        val descripcionTextView: TextView = view.findViewById(R.id.txtDescripcion)
        val montoTextView: TextView = view.findViewById(R.id.txtMonto)
        val activoTextView: TextView = view.findViewById(R.id.txtActivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.clienteTextView.text = "Cliente: ${pedido.nombreCliente}"
        holder.direccionTextView.text = "Dirección: ${pedido.direccion}"
        holder.descripcionTextView.text = "Descripción: ${pedido.descripcion}"
        holder.montoTextView.text = "Monto: $${pedido.monto}"
        holder.activoTextView.text = "Activo: ${if (pedido.activo) "Sí" else "No"}"
    }

    override fun getItemCount() = pedidos.size
}
