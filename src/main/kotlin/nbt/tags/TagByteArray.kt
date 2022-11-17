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

class TagByteArray(override val value: ByteArray) : NbtValueTag<ByteArray>(ByteArray::class.java) {
    override val payloadSize = 4 + value.size
    override val typeId = NbtTypeId.BYTE_ARRAY

    override fun write(stream: DataOutputStream) {
        stream.writeInt(value.size)
        for (byte in value) {
            stream.writeByte(byte.toInt())
        }
    }

    override fun toString() = toString(StringBuilder(), 0, WriterState.COMPOUND).toString()

    override fun toString(sb: StringBuilder, indentLevel: Int, writerState: WriterState): StringBuilder {
        value.joinTo(buffer = sb, separator = ", ", prefix = "bytes(", postfix = ")")
        return sb
    }

    override fun valueEquals(otherValue: ByteArray): Boolean {
        return this.value.contentEquals(otherValue)
    }

    // This makes IntelliJ happy - and angry at the same time
    @Suppress("RedundantOverride")
    override fun equals(other: Any?) = super.equals(other)

    override fun hashCode() = this.value.contentHashCode()

    override fun valueCopy() = value.copyOf(value.size)
}
