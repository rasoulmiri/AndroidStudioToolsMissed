package androidstudio.tools.missed.features.customcommand.model

data class CustomCommand(
    val id: Int?,
    val index: Int?,
    val name: String?,
    val description: String?,
    val command: String?
) {
    companion object {
        val EMPTY = CustomCommand(id = null, index = null, name = null, description = null, command = null)
    }
}
