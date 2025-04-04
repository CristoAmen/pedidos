data class Pedido(
    val folio: String = "",
    val usuarioId: String = "",
    val nombreCliente: String = "",
    var direccion: String = "",
    var descripcion: String = "",
    var monto: Double = 0.0,
    var activo: Boolean = true
)
