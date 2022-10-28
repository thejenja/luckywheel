package com.servegame.yeyyyyyy.luckywheel.commands

import com.servegame.yeyyyyyy.luckywheel.LuckyWheel
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LuckyWheelCommand : CommandExecutor {
    private val logger = LuckyWheel.plugin.logger
    private val messagesConfig = LuckyWheel.plugin.messagesFileManager.getMessagesConfig()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            logger.info(ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("player_only_command")!!))
            return false
        }
        val player: Player = sender
        if (!player.hasPermission("luckywheel")) {
            send(player, "luckywheel_command_no_permission")
            return false
        }
        if (args.isNotEmpty()) {
            send(player, "invalid_parameter")
            return false
        }
        // Do something

        return true
    }

    private fun send(player: Player, text: String) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('$', text))
    }
}