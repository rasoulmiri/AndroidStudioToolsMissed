package androidstudio.tools.missed.features.customcommand.domain

import androidstudio.tools.missed.features.customcommand.model.CustomCommand
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

@Suppress("MagicNumber")
class CustomCommandRepository {
    private val gson = Gson()
    private val filePath: String

    init {
        val userHome = System.getProperty("user.home")
        val pluginFolderPath = "$userHome/.AndroidStudioToolsMissed"
        filePath = "$pluginFolderPath/custom_command.json"
        val pluginFolder = File(pluginFolderPath)
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs()
            saveDefaultCustomCommand()
        }
    }

    private fun saveDefaultCustomCommand() {
        writeToFile(
            listOf(
                CustomCommand(
                    id = generateUniqueId(),
                    index = 0,
                    name = "Clears Cache",
                    description = "Clears the cache for a specific app",
                    command = "\$ADB shell pm clear \$APP_ID"
                ),
                CustomCommand(
                    id = generateUniqueId(),
                    index = 1,
                    name = "Close and reopen ",
                    description = "Close and reopen a specific app",
                    command = "\$ADB shell am force-stop \$APP_ID;\nmonkey -p \$APP_ID 1"
                )
            )
        )
    }

    fun loadAll(): List<CustomCommand> {
        val file = File(filePath)
        if (file.exists()) {
            val fileContent = file.readText()
            return gson.fromJson(fileContent, object : TypeToken<List<CustomCommand>>() {}.type)
        }
        return emptyList()
    }

    fun save(customCommand: CustomCommand) {
        val existingData = loadAll().toMutableList()
        val index = existingData.indexOfFirst { it.id == customCommand.id }
        if (index != -1) {
            existingData[index] = customCommand
        } else {
            existingData.add(customCommand)
        }
        writeToFile(existingData)
    }

    fun deleteById(id: Int) {
        val existingData = loadAll().toMutableList()
        val updatedData = existingData.filter { it.id != id }
        writeToFile(updatedData)
    }

    private fun writeToFile(data: List<CustomCommand>) {
        try {
            val file = File(filePath)
            file.writeText(gson.toJson(data))
            println("Data saved to $filePath")
        } catch (e: IOException) {
            println("Error saving data to file: ${e.message}")
        }
    }

    fun generateUniqueId() = (System.currentTimeMillis() / 1000).toInt()

    fun getNextIndex() = loadAll().size + 1
}
