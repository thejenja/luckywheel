package com.servegame.yeyyyyyy.luckywheel.core.models

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.random.Random

class LootTable (
    private var loots: MutableList<Loot> = mutableListOf(Loot(ItemStack(Material.DIAMOND_SWORD), 0.2), Loot(ItemStack(Material.DIRT, 64))),
    override val size: Int = loots.size
) : MutableCollection<Loot> {
    val maxLootTableSize = 53
    val totalProbability
        get() = loots.fold(0.0) { acc, loot -> acc + loot.weight }
    val inventorySize
        get() = ceil((loots.size + 1) / 9.0).toInt() * 9

    init {
        loots = loots.subList(0, minOf(maxLootTableSize - 1, loots.size))
    }

    fun getRandomLoot(): Pair<Loot, String> {
        val randomNumber = Random.nextDouble() * totalProbability
        var summedWeight = 0.0

        // goes through the loots list until randomNumber is lower than the summedWeight, then return the current loot
        for (loot in loots) {
            summedWeight += loot.weight
            if (randomNumber < summedWeight) {
                return Pair(loot, getProbabilityOfLootFormatted(loot))
            }
        }
        throw Exception("End of loots reached but no Loot was returned!")
    }

    /**
     * Provide the index of a Loot to get its probability to be chosen
     * @return the probability for the loot to be chosen from 0 (0%) to 1 (100%), or -1 if the index is out of bound
     */
    fun getProbabilityOfLootAt(index: Int): Double {
        if (index in 0.until(loots.size)) {
            return loots[index].weight / totalProbability
        }
        return -1.0
    }

    /**
     * Provide a loot to get its probability to be chosen
     * @return the probability for the loot to be chosen from 0 (0%) to 1 (100%), or -1 if the loot is not in the LootTable
     */
    fun getProbabilityOfLoot(loot: Loot): Double {
        val index = loots.indexOf(loot)
        return getProbabilityOfLootAt(index)
    }

    override fun contains(element: Loot): Boolean {
        return loots.contains(element)
    }

    override fun containsAll(elements: Collection<Loot>): Boolean {
        return loots.containsAll(elements)
    }

    /**
     * Adds the specified element to the end of this list.
     *
     * @return `true` if the item was added, or `false` if `maxLootTableSize` has been reached
     */
    override fun add(element: Loot): Boolean {
        if (loots.size >= maxLootTableSize) return false
        loots.add(element)
        return true
    }

    override fun addAll(elements: Collection<Loot>): Boolean {
        for(loot in elements) {
            if (loots.size >= maxLootTableSize) return false
            loots.add(loot)
        }
        return true
    }

    override fun clear() {
        loots.clear()
    }

    override fun isEmpty(): Boolean {
        return loots.isEmpty()
    }

    override fun iterator(): MutableIterator<Loot> {
        return loots.map { loot -> loot.copy() }.toMutableList().iterator()
    }

    override fun retainAll(elements: Collection<Loot>): Boolean {
        return loots.retainAll(elements)
    }

    override fun removeAll(elements: Collection<Loot>): Boolean {
        return loots.removeAll(elements)
    }

    override fun remove(element: Loot): Boolean {
        return loots.remove(element)
    }

    fun getProbabilityOfLootFormatted(loot: Loot): String {
        return "%.2f".format(getProbabilityOfLoot(loot).times(100)) + "%"
    }

    fun getAllItems(): Collection<ItemStack> {
        return loots.map { (item) -> item.clone() }
    }
}