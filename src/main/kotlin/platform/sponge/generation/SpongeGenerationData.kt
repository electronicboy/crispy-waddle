/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.sponge.generation

import com.demonwav.mcdev.insight.generation.GenerationData

data class SpongeGenerationData(val isIgnoreCanceled: Boolean, val eventOrder: String) : GenerationData
