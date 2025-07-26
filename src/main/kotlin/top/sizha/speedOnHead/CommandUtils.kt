package top.sizha.speedOnHead

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecordSpeedCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when {
            !sender.hasPermission("speedOnHead.record") -> {
                sender.sendMessage("你没有权限执行该命令")
                return true
            }

            args?.isEmpty()!! -> {
                sender.sendMessage("当前的记录速度为${UniversalDataManager.recordSpeed}")
                return true
            }

            args.size == 1 -> {
                var newSpeed = 5
                try {
                    newSpeed = args[0].toInt()
                } catch (_: NumberFormatException) {
                    sender.sendMessage("错误的命令格式")
                    return false
                } finally {
                    UniversalDataManager.recordSpeed = newSpeed
                }
                return true
            }

            else -> return false
        }
    }
}

class NoticeSpeedCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when {
            !sender.hasPermission("speedOnHead.notice") -> {
                sender.sendMessage("你没有权限执行该命令")
                return true
            }

            args?.isEmpty()!! -> {
                sender.sendMessage("当前的警告速度为${UniversalDataManager.noticeSpeed}")
                return true
            }

            args.size == 1 -> {
                var newSpeed = 5
                try {
                    newSpeed = args[0].toInt()
                } catch (_: NumberFormatException) {
                    sender.sendMessage("错误的命令格式")
                    return false
                } finally {
                    UniversalDataManager.noticeSpeed = newSpeed
                }
                return true
            }

            else -> return false
        }
    }
}

class StartCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("speedOnHead.start")) {
            sender.sendMessage("你没有执行该命令的权限")
            return true
        }
        try {
            sender.sendMessage("开始检测")
            UniversalDataManager.lock.lock()
            UniversalDataManager.resultFile = File(
                UniversalDataManager.dataDir,
                "results-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))}.csv"
            )
            CSVManager.startWriteResult(UniversalDataManager.resultFile!!)

            UniversalDataManager.isStart = true
        } finally {
            UniversalDataManager.lock.unlock()
        }
        return true
    }
}

class StopCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("speedOnHead.stop")) {
            sender.sendMessage("你没有执行该命令的权限")
            return true
        }
        try {
            sender.sendMessage("结束检测")
            UniversalDataManager.lock.lock()
            UniversalDataManager.resultFile = null
            UniversalDataManager.isStart = false
        } finally {
            UniversalDataManager.lock.unlock()
        }
        return true
    }
}