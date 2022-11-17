/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp

import com.demonwav.mcdev.platform.mcp.srg.SrgType
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager

@State(name = "McpModuleSettings", storages = [Storage(StoragePathMacros.MODULE_FILE)])
class McpModuleSettings : PersistentStateComponent<McpModuleSettings.State> {

    data class State(
        var minecraftVersion: String? = null,
        var mcpVersion: String? = null,
        var mappingFile: String? = null,
        var srgType: SrgType? = null,
        var platformVersion: String? = null
    )

    private var state: State = State(srgType = SrgType.SRG)

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(module: Module): McpModuleSettings {
            // TODO: Migrate these to the facet's settings

            val settings = module.getService(McpModuleSettings::class.java) as McpModuleSettings
            if (settings.getState().minecraftVersion != null) {
                return settings
            }

            // Attempt to find settings on the parent module
            val manager = ModuleManager.getInstance(module.project)
            val path = manager.getModuleGroupPath(module) ?: return settings
            val parent = manager.findModuleByName(path.last()) ?: return settings

            val newSettings = parent.getService(McpModuleSettings::class.java) as McpModuleSettings
            if (newSettings.getState().minecraftVersion == null) {
                return settings
            }
            return newSettings
        }
    }
}
