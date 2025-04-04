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
    private val onClick: (Pedido, Int) -> Unit,
    private val onEdit: (Pedido, Int) -> Unit,
    private val onCancel: (Pedido, Int) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        val txtMonto: TextView = itemView.findViewById(R.id.txtMonto)
        // Referencias a los botones de acción
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditar)
        val btnCancelar: MaterialButton = itemView.findViewById(R.id.btnCancelar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.txtCliente.text = pedido.nombreCliente
        holder.txtDescripcion.text = pedido.descripcion
        holder.txtDireccion.text = pedido.direccion
        holder.txtMonto.text = "$${pedido.monto}"

        // Listener para el item completo (por ejemplo, para ver detalles)
        holder.itemView.setOnClickListener {
            onClick(pedido, position)
        }

        // Listener para editar el pedido
        holder.btnEditar.setOnClickListener {
            onEdit(pedido, position)
        }

        // Listener para cancelar el pedido
        holder.btnCancelar.setOnClickListener {
            onCancel(pedido, position)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size
}
