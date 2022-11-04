package com.servegame.yeyyyyyy.luckywheel.menusystem

import com.servegame.yeyyyyyy.luckywheel.LuckyWheel
import com.servegame.yeyyyyyy.luckywheel.core.Wheel
import com.servegame.yeyyyyyy.luckywheel.core.models.LootTable
import com.servegame.yeyyyyyy.luckywheel.extensions.spawnParticles
import com.servegame.yeyyyyyy.luckywheel.utils.ParticleEffect
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class MenuListener : Listener {
    val messagesConfig = LuckyWheel.plugin.messagesFileManager.getConfig()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        when (event.view.title) {
            MenuTitle.MainMenu.title -> onMainMenuClick(event)
            MenuTitle.LootTableMenu.title -> onLootTableMenuClick(event)
            MenuTitle.WheelMenu.title -> onWheelMenuClick(event)
        }
    }

    private fun onMainMenuClick(event: InventoryClickEvent) {
        event.isCancelled = true
        when (event.currentItem?.itemMeta?.displayName) {
            MenuOptions.SpinTheWheel.option -> {
                val menu = Menu()
                menu.openWheelGui(event.whoClicked as Player, LootTable())
            }
            MenuOptions.ShowLootTable.option -> {
                val menu = Menu()
                menu.openLootTableGui(event.whoClicked as Player, LootTable())
            }
            MenuOptions.ExitMenu.option -> {
                event.whoClicked.closeInventory()
            }
        }
        return
    }

    private fun onLootTableMenuClick(event: InventoryClickEvent) {
        event.isCancelled = true
        when (event.currentItem?.itemMeta?.displayName) {
            MenuOptions.GoBack.option -> {
                val menu = Menu()
                menu.openMainMenuGui(event.whoClicked as Player)
            }
            MenuOptions.ExitMenu.option -> {
                event.whoClicked.closeInventory()
            }
        }
        return
    }

    private fun onWheelMenuClick(event: InventoryClickEvent) {
        event.isCancelled = true
        when (event.currentItem?.itemMeta?.displayName) {
            MenuOptions.SpinTheWheel.option -> {
                event.inventory.remove(event.currentItem!!)
                event.inventory.setItem(9 + 4, ItemStack(Material.SPECTRAL_ARROW))
                val player = event.whoClicked as Player
                val wheelMeta = player.getMetadata("luckywheel_wheel")
                val wheel = wheelMeta[0].value() as Wheel
                wheel.spin()
                player.spawnParticles(ParticleEffect(Particle.VILLAGER_HAPPY, player.location))
                player.playSound(player, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 1f)
            }
            MenuOptions.GoBack.option -> {
                val menu = Menu()
                menu.openMainMenuGui(event.whoClicked as Player)
            }
            MenuOptions.ExitMenu.option -> {
                event.whoClicked.closeInventory()
            }
        }
        return
    }
}