data class Pedido(
    val folio: String = "",
    val usuarioId: String = "",
    val nombreCliente: String = "",
    val direccion: String = "",
    val descripcion: String = "",
    val monto: Double = 0.0,
    val activo: Boolean = true
)