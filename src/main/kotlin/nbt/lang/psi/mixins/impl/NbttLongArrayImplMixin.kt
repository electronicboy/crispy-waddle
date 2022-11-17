/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.nbt.lang.psi.mixins.impl

import com.demonwav.mcdev.nbt.lang.psi.mixins.NbttLongArrayMixin
import com.demonwav.mcdev.nbt.tags.TagLongArray
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class NbttLongArrayImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), NbttLongArrayMixin {

    override fun getLongArrayTag(): TagLongArray {
        return TagLongArray(getLongList().map { it.getLongTag().value }.toLongArray())
    }
}
