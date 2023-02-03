package me.julinator99.assistant.salesforce

import org.bukkit.plugin.java.JavaPlugin


class AssistantSalesforce : JavaPlugin() {
	companion object {
		lateinit var plugin: AssistantSalesforce
			private set
	}
	
	init {
		plugin = this
	}
}