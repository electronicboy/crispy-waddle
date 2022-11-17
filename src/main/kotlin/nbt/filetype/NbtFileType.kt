/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.nbt.filetype

import com.demonwav.mcdev.asset.PlatformAssets
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile

object NbtFileType : FileType {
    override fun getDefaultExtension() = "nbt"
    override fun getIcon() = PlatformAssets.MINECRAFT_ICON
    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null
    override fun getName() = "NBT"
    override fun getDescription() = "NBT"
    override fun isBinary() = true
    override fun isReadOnly() = false
}
