package com.iroselle.autogamerule.bukkit

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.plugin.java.JavaPlugin

class AutoGameRule : JavaPlugin(), Listener {

    override fun onEnable() {
        saveDefaultConfig()

        for (w in Bukkit.getWorlds()) {
            setGameRule(w)
        }

        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler
    fun onRun(e : WorldLoadEvent) {
        setGameRule(e.world)
    }

    private fun setGameRule(w : World) {
        if (!config.getStringList("disabled-worlds").contains(w.name)) return

        for (rule in w.gameRules) {
            var allow = false
            var cRule : String? = null
            config.getConfigurationSection("all-worlds-gamerules")?.getKeys(false)!!.forEach {
                if (it.toUpperCase() == rule) {
                    allow = true
                    cRule = it
                    return
                }
            }

            if (!allow || cRule == null) continue

            val value = config.get("all-worlds-gamerules.${cRule}")

            if (!w.setGameRuleValue(rule, "$value")) {
                println(" ")
                println("[AutoGameRule] 世界 ${w.name} 的游戏规则 $rule 的值 $value 设置失败!")
                println(" ")
            }

        }
    }

}