package top.sizha.speedOnHead

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import top.sizha.speedOnHead.LocationManager.locationMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SpeedOnHead : JavaPlugin() {
    @Suppress("UnstableApiUsage", "Deprecation")
    override fun onEnable() {
        // Plugin startup logic
        saveResource("config.yml", false)
        saveDefaultConfig()
        UniversalDataManager.analyseConfig(config)
        getCommand("record_speed")?.setExecutor(RecordSpeedCommand())
        getCommand("notice_speed")?.setExecutor(NoticeSpeedCommand())
        getCommand("start_record")?.setExecutor(StartCommand())
        getCommand("stop_record")?.setExecutor(StopCommand())
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                Bukkit.getScheduler().runTask(this@SpeedOnHead, Runnable {
                    var result = ""
                    val sources = mutableListOf<Player>()
                    for (player in Bukkit.getOnlinePlayers()) {
                        // Speed Calculation
                        val curLocation = player.location
                        val curTime = System.currentTimeMillis()
                        val lastLocation = locationMap[player.uniqueId]?.first
                        val lastTime = locationMap[player.uniqueId]?.second
                        if (lastLocation == null || lastTime == null) {
                            locationMap[player.uniqueId] = Pair(curLocation, curTime)
                            continue
                        }
                        val duration = curTime - lastTime
                        if (duration < 20) {
                            continue
                        }
                        val speed = (lastLocation.distance(curLocation) / duration) * 1000
//                        Bukkit.getLogger().info("Switch 0")
//                        player.sendActionBar("Velocity:${speed * 1000}\n duration:${duration}")
//                        player.sendTitle("Velocity:${speed * 1000},duration:${duration}", "", 20, 10, 20)
                        if (((speed > UniversalDataManager.recordSpeed) and (UniversalDataManager.isStart)) and (speed < UniversalDataManager.maxSpeed)) {
//                            Bukkit.getLogger().info("Switch 1")
                            if (UniversalDataManager.resultFile != null) {
                                val resultFile = UniversalDataManager.resultFile!!
                                CSVManager.appendResult(
                                    resultFile,
                                    player.uniqueId.toString(),
                                    player.name,
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
                                        .toString(),
                                    speed
                                )
                            }
                        }
                        if ((speed > UniversalDataManager.noticeSpeed) and (speed < UniversalDataManager.maxSpeed)) {
//                            Bukkit.getLogger().info("Switch 2")
                            result += "${player.name}:${String.format("%.2f", speed)}m/s;"
                            sources.add(player)
//                            Bukkit.getLogger().info("${player.name}'s speed is ${speed}!")
                        }
                        locationMap[player.uniqueId] = Pair(curLocation, curTime)
                    }
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (result != "") {
                            for (source in sources) {
                                player.playSound(source, "minecraft:block.note_block.pling", 1.0f, 1.0f)
                            }
                            player.sendActionBar("超速！$result")
                        }
                    }
                })
                delay(500)
            }

        }
        Bukkit.getLogger().info("SpeedOnHead plugin successfully loaded!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

}

object LocationManager {
    val locationMap = HashMap<UUID, Pair<Location, Long>>()
}

