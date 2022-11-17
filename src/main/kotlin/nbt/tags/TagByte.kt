/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.nbt.tags

import java.io.DataOutputStream

class TagByte(override val value: Byte) : NbtValueTag<Byte>(Byte::class.java) {
    override val payloadSize = 1
    override val typeId = NbtTypeId.BYTE

    override fun write(stream: DataOutputStream) {
        stream.writeByte(value.toInt())
    }

    override fun toString() = toString(StringBuilder(), 0, WriterState.COMPOUND).toString()

    override fun toString(sb: StringBuilder, indentLevel: Int, writerState: WriterState) =
        sb.append(value).append('B')!!
}
