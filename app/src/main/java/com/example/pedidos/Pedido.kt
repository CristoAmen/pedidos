data class Pedido(
    val folio: String = "",
    val usuarioId: String = "",
    val nombreCliente: String = "",
    val direccion: String = "",
    val descripcion: String = "",
    val monto: Double = 0.0,
    val activo: Boolean = true
) {
    init {
        require(monto >= 0) { "El monto no puede ser negativo" }
        require(nombreCliente.isNotBlank()) { "El nombre del cliente no puede estar vacío" }
    }
}