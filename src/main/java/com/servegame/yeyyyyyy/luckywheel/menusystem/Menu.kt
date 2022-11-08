package com.servegame.yeyyyyyy.luckywheel.menusystem

import com.servegame.yeyyyyyy.luckywheel.LuckyWheel
import com.servegame.yeyyyyyy.luckywheel.core.Wheel
import com.servegame.yeyyyyyy.luckywheel.core.models.LootTable
import com.servegame.yeyyyyyy.luckywheel.extensions.currentLootTable
import com.servegame.yeyyyyyy.luckywheel.extensions.getColoredString
import com.servegame.yeyyyyyy.luckywheel.utils.getNearestCeilMultipleOfNine
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

class Menu {

    companion object {
        val messagesConfig = LuckyWheel.plugin.messagesFileManager.getConfig()
        val lootTableFileManager = LuckyWheel.plugin.lootTablesFileManager
        fun openMainMenuGui(player: Player) {
            val inv = Bukkit.createInventory(null, 9, MenuTitle.MainMenu.title)

            addItem(MenuItem.spinTheWheel.copy(), inv)
            addItem(MenuItem.showLootTable.copy(), inv)
            addItem(MenuItem.exitMenu.copy(pos = inv.size - 1), inv)
            player.openInventory(inv)
        }

        fun openLootTableListGui(player: Player) {
            val lootTables = lootTableFileManager.getAllLootTables()
            val invSize = getNearestCeilMultipleOfNine(lootTables.size + 2)
            val inv = Bukkit.createInventory(null, invSize, MenuTitle.LootTableListMenu.title)
            var i = 0
            lootTables.forEach { lootTable ->
                addItem(
                    MenuItem(
                        Material.values()[167 + i],
                        ChatColor.translateAlternateColorCodes('&', "&2" + lootTable.name),
                        i++
                    ), inv
                )
            }

            player.openInventory(inv)
        }

        fun openLootTableGui(player: Player, lootTable: LootTable) {
            val invSize = getNearestCeilMultipleOfNine(lootTable.inventorySize + 3)
            val inv = Bukkit.createInventory(null, invSize, MenuTitle.LootTableMenu.title)
            insertLootTableInInventory(lootTable, inv, player)

            val lootTableMeta = FixedMetadataValue(LuckyWheel.plugin, lootTable.name)
            player.setMetadata("luckywheel_loot_table", lootTableMeta)
            addItem(MenuItem(Material.PAPER, messagesConfig.getColoredString("left_click_to_add"), inv.size - 3), inv)
            addItem(MenuItem.goBack.copy(pos = inv.size - 2), inv)
            addItem(MenuItem.exitMenu.copy(pos = inv.size - 1), inv)
            player.openInventory(inv)
        }

        fun openWheelGui(player: Player, lootTable: LootTable) {
            val inv = Bukkit.createInventory(null, 27, MenuTitle.WheelMenu.title)

            val wheel = Wheel(lootTable, inv, player)
            paintWheelRow(inv, wheel)
            val wheelMeta = FixedMetadataValue(LuckyWheel.plugin, wheel)
            player.setMetadata("luckywheel_wheel", wheelMeta)

            (9..17).forEach { pos -> addItem(MenuItem(Material.WHITE_STAINED_GLASS_PANE, "-", pos), inv) }
            addItem(MenuItem.spinTheWheel.copy(pos = 9 + 4), inv)
            addItem(MenuItem.goBack.copy(pos = inv.size - 2), inv)
            addItem(MenuItem.exitMenu.copy(pos = inv.size - 1), inv)
            player.openInventory(inv)
        }

        fun showEditItemWeightGui(player: Player, item: ItemStack) {
            val inv = Bukkit.createInventory(null, 9, MenuTitle.EditItemWeightMenu.title)
            addItem(MenuItem(Material.CRIMSON_PLANKS, MenuOptions.Sub1.option, 0), inv)
            addItem(MenuItem(Material.CRIMSON_STAIRS, MenuOptions.Sub01.option, 1), inv)
            addItem(MenuItem(Material.CRIMSON_SLAB, MenuOptions.Sub001.option, 2), inv)
            addItem(MenuItem(Material.CRIMSON_PRESSURE_PLATE, MenuOptions.Sub0001.option, 3), inv)
            addItem(MenuItem(Material.WARPED_PRESSURE_PLATE, MenuOptions.Add0001.option, 5), inv)
            addItem(MenuItem(Material.WARPED_SLAB, MenuOptions.Add001.option, 6), inv)
            addItem(MenuItem(Material.WARPED_STAIRS, MenuOptions.Add01.option, 7), inv)
            addItem(MenuItem(Material.WARPED_PLANKS, MenuOptions.Add1.option, 8), inv)


            val lootTable = lootTableFileManager.getLootTable(player.currentLootTable())
            val loot = lootTable.getLoot(item)
            val itemClone = loot.item.clone()
            val meta = itemClone.itemMeta!!
            meta.lore = listOf(
                messagesConfig.getColoredString("current_weight").replace("{weight}", loot.weight.toString()),
                messagesConfig.getColoredString("left_click_to_accept"),
                messagesConfig.getColoredString("right_click_to_undo")
            )
            itemClone.itemMeta = meta
            inv.setItem(4, itemClone)
            player.openInventory(inv)
        }


        fun paintWheelRow(inv: Inventory, wheel: Wheel) {
            var index = 0
            wheel.itemRow.subList(0, 9).forEach { item -> inv.setItem(index++, item) }
        }


        private fun addItem(menuItem: MenuItem, inv: Inventory) {
            val (material, text, pos) = menuItem
            val item = ItemStack(material)
            setItemDisplayName(item, text)
            inv.setItem(pos, item)
        }

        private fun setItemDisplayName(spinItem: ItemStack, name: String) {
            val meta = spinItem.itemMeta!!
            meta.setDisplayName(name)
            spinItem.itemMeta = meta
        }

        private fun insertLootTableInInventory(
            lootTable: LootTable,
            inv: Inventory,
            player: Player
        ) {
            var index = 0
            lootTable.forEach { loot ->
                val meta = loot.item.itemMeta!!
                meta.lore = mutableListOf(
                    messagesConfig.getColoredString("common_probability") + lootTable.getProbabilityOfLootFormatted(loot),
                    if (player.hasPermission("luckywheel.loottables.edit")) messagesConfig.getColoredString("right_click_to_remove") else ""
                )
                val lootCopy = loot.copy(item = loot.item.clone())
                lootCopy.item.itemMeta = meta
                inv.setItem(index++, lootCopy.item)
            }
        }


    }

}