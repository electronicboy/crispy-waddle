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

class TagFloat(override val value: Float) : NbtValueTag<Float>(Float::class.java) {
    override val payloadSize = 4
    override val typeId = NbtTypeId.FLOAT

    override fun write(stream: DataOutputStream) {
        stream.writeFloat(value)
    }

    override fun toString() = toString(StringBuilder(), 0, WriterState.COMPOUND).toString()

    override fun toString(sb: StringBuilder, indentLevel: Int, writerState: WriterState) =
        sb.append(value).append('F')!!
}
