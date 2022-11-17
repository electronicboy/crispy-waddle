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

class TagLong(override val value: Long) : NbtValueTag<Long>(Long::class.java) {
    override val payloadSize = 8
    override val typeId = NbtTypeId.LONG

    override fun write(stream: DataOutputStream) {
        stream.writeLong(value)
    }

    override fun toString() = toString(StringBuilder(), 0, WriterState.COMPOUND).toString()

    override fun toString(sb: StringBuilder, indentLevel: Int, writerState: WriterState) =
        sb.append(value).append('L')!!
}
