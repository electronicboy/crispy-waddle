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

import com.demonwav.mcdev.facet.MinecraftFacet
import com.demonwav.mcdev.platform.AbstractModuleType
import com.demonwav.mcdev.platform.PlatformType
import com.demonwav.mcdev.platform.mcp.util.McpConstants
import com.demonwav.mcdev.util.CommonColors
import com.demonwav.mcdev.util.SemanticVersion
import javax.swing.Icon

object McpModuleType : AbstractModuleType<McpModule>("", "") {

    private const val ID = "MCP_MODULE_TYPE"

    init {
        CommonColors.applyStandardColors(colorMap, McpConstants.TEXT_FORMATTING)
        CommonColors.applyStandardColors(colorMap, McpConstants.CHAT_FORMATTING)
    }

    override val platformType = PlatformType.MCP
    override val icon: Icon? = null
    override val id = ID
    override val ignoredAnnotations = emptyList<String>()
    override val listenerAnnotations = emptyList<String>()
    override val hasIcon = false

    override fun generateModule(facet: MinecraftFacet) = McpModule(facet)

    val MC_1_12_2 = SemanticVersion.release(1, 12, 2)
}
