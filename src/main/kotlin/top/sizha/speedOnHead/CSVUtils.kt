package top.sizha.speedOnHead

import org.bukkit.Bukkit
import java.io.File
import java.io.FileWriter
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Level

object CSVManager {
    private val plugin = Bukkit.getPluginManager().getPlugin("SpeedOnHead") as SpeedOnHead
    private val dataDir = File(plugin.dataFolder, "results")
    private val lock = ReentrantLock()

    init {
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
    }

    fun startWriteResult(resultFile: File) {
        try {
            lock.lock()
            try {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                    FileWriter(resultFile, true).use { writer ->
                        writer.appendLine("PlayerUUID,PlayerName,Time,Velocity")
                    }
                })
            } catch (_: Exception) {
                plugin.logger.log(Level.SEVERE, "无法写入result，请检查是否有写入权限")
            }

        } finally {
            lock.unlock()
        }

    }

    fun appendResult(resultFile: File, uuid: String, username: String, time: String, velocity: Double) {
        try {
            lock.lock()
            try {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                    FileWriter(resultFile, true).use { writer ->
                        writer.appendLine("${uuid},${username},${time},${velocity}")
                    }
                })
            } catch (_: Exception) {
                plugin.logger.log(Level.SEVERE, "无法写入result，请检查是否有写入权限")
            }
        } finally {
            lock.unlock()
        }
    }
}