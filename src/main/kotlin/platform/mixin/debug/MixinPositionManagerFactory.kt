/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mixin.debug

import com.intellij.debugger.PositionManager
import com.intellij.debugger.PositionManagerFactory
import com.intellij.debugger.engine.DebugProcess

class MixinPositionManagerFactory : PositionManagerFactory() {

    override fun createPositionManager(process: DebugProcess): PositionManager? = MixinPositionManager(process)
}
