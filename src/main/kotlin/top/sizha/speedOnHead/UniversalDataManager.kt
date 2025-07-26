package top.sizha.speedOnHead

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import java.util.concurrent.locks.ReentrantLock

object UniversalDataManager {
    var isStart = false
    var recordSpeed = 25
    var noticeSpeed = 30
    var maxSpeed = 500
    val lock = ReentrantLock()
    private val plugin = Bukkit.getPluginManager().getPlugin("SpeedOnHead") as SpeedOnHead
    val dataDir = File(plugin.dataFolder, "results")
    var resultFile: File? = null

    // Control Flow
    fun analyseConfig(config: FileConfiguration) {
        recordSpeed = config.getInt("recordSpeed")
        noticeSpeed = config.getInt("noticeSpeed")
    }

}