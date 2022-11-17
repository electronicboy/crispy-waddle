/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.toml.platform.forge.inspections

import com.demonwav.mcdev.platform.forge.util.ForgeConstants
import com.demonwav.mcdev.toml.TomlElementVisitor
import com.demonwav.mcdev.toml.platform.forge.ModsTomlSchema
import com.demonwav.mcdev.toml.stringValue
import com.demonwav.mcdev.toml.tomlType
import com.demonwav.mcdev.util.SemanticVersion
import com.demonwav.mcdev.util.findMcpModule
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import org.toml.lang.psi.TomlArrayTable
import org.toml.lang.psi.TomlHeaderOwner
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.TomlKeyValueOwner
import org.toml.lang.psi.TomlTableHeader
import org.toml.lang.psi.TomlValue
import org.toml.lang.psi.ext.name

class ModsTomlValidationInspection : LocalInspectionTool() {

    override fun getDisplayName(): String = "Forge's mods.toml validation"

    override fun getStaticDescription(): String = "Checks mods.toml files for errors"

    override fun processFile(file: PsiFile, manager: InspectionManager): MutableList<ProblemDescriptor> {
        if (file.virtualFile.name == ForgeConstants.MODS_TOML) {
            return super.processFile(file, manager)
        }
        return mutableListOf()
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        if (holder.file.virtualFile.name == ForgeConstants.MODS_TOML) {
            return Visitor(holder)
        }
        return PsiElementVisitor.EMPTY_VISITOR
    }

    private class Visitor(val holder: ProblemsHolder) : TomlElementVisitor() {

        override fun visitKeyValue(keyValue: TomlKeyValue) {
            when (keyValue.key.text) {
                "modId" -> {
                    val value = keyValue.value ?: return
                    val modId = value.stringValue() ?: return
                    if (!ForgeConstants.MOD_ID_REGEX.matches(modId)) {
                        val endOffset = if (value.text.endsWith('"')) modId.length + 1 else modId.length
                        holder.registerProblem(value, TextRange(1, endOffset), "Mod ID is invalid")
                    }
                }
                "displayTest" -> {
                    val value = keyValue.value ?: return
                    val test = value.stringValue() ?: return
                    if (test !in ForgeConstants.DISPLAY_TESTS) {
                        val endOffset = if (value.text.endsWith('"')) test.length + 1 else test.length
                        holder.registerProblem(value, TextRange(1, endOffset), "DisplayTest $test does not exist")
                    }

                    val forgeVersion = runCatching {
                        keyValue.findMcpModule()?.getSettings()?.platformVersion?.let(SemanticVersion::parse)
                    }.getOrNull()
                    val minVersion = ForgeConstants.DISPLAY_TEST_MANIFEST_VERSION
                    if (forgeVersion != null && forgeVersion < minVersion) {
                        holder.registerProblem(keyValue.key, "DisplayTest is only available since $minVersion")
                    }
                }
                "side" -> {
                    val value = keyValue.value ?: return
                    val side = value.stringValue() ?: return
                    if (side !in ForgeConstants.DEPENDENCY_SIDES) {
                        val endOffset = if (value.text.endsWith('"')) side.length + 1 else side.length
                        holder.registerProblem(value, TextRange(1, endOffset), "Side $side does not exist")
                    }
                }
                "ordering" -> {
                    val value = keyValue.value ?: return
                    val order = value.stringValue() ?: return
                    if (order !in ForgeConstants.DEPENDENCY_ORDER) {
                        val endOffset = if (value.text.endsWith('"')) order.length + 1 else order.length
                        holder.registerProblem(value, TextRange(1, endOffset), "Order $order does not exist")
                    }
                }
            }
        }

        override fun visitKeySegment(keySegment: TomlKeySegment) {
            val key = keySegment.parent as? TomlKey ?: return
            if (key.parent is TomlTableHeader &&
                key.segments.indexOf(keySegment) == 1 &&
                key.segments.first().text == "dependencies" // We are visiting a dependency table
            ) {
                val targetId = keySegment.text
                val isDeclaredId = keySegment.containingFile.children
                    .filterIsInstance<TomlArrayTable>()
                    .filter { it.header.key?.name == "mods" }
                    .any { it.entries.find { it.key.text == "modId" }?.value?.stringValue() == targetId }
                if (!isDeclaredId) {
                    holder.registerProblem(keySegment, "Mod $targetId is not declared in this file")
                }
            }
        }

        override fun visitValue(value: TomlValue) {
            val schema = ModsTomlSchema.get(value.project)
            val key = value.parentOfType<TomlKeyValue>()?.key?.text ?: return
            val (expectedType, actualType) = when (val parent = value.parent) {
                is TomlKeyValue -> when (val table = value.parentOfType<TomlKeyValueOwner>()) {
                    is TomlHeaderOwner ->
                        table.header.key?.segments?.firstOrNull()?.text?.let { schema.tableEntry(it, key)?.type }
                    null -> schema.topLevelEntries.find { it.key == key }?.type
                    else -> return
                } to parent.value?.tomlType
                else -> return
            }
            if (expectedType != null && actualType != null && expectedType != actualType) {
                holder.registerProblem(value, "Wrong value type, expected ${expectedType.presentableName}")
            }
        }
    }
}
