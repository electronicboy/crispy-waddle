/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.nbt.lang.format

import com.demonwav.mcdev.nbt.lang.NbttLanguage
import com.demonwav.mcdev.nbt.lang.gen.psi.NbttTypes
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.Indent
import com.intellij.formatting.SpacingBuilder
import com.intellij.psi.codeStyle.CodeStyleSettings

class NbttFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val block = NbttBlock(formattingContext.node, formattingContext.codeStyleSettings, Indent.getNoneIndent(), null)
        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile,
            block,
            formattingContext.codeStyleSettings
        )
    }

    companion object {
        fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
            val nbttSettings = settings.getCustomSettings(NbttCodeStyleSettings::class.java)
            val commonSettings = settings.getCommonSettings(NbttLanguage)

            val spacesBeforeComma = if (commonSettings.SPACE_BEFORE_COMMA) 1 else 0
            val spacesBeforeColon = if (nbttSettings.SPACE_BEFORE_COLON) 1 else 0
            val spacesAfterColon = if (nbttSettings.SPACE_AFTER_COLON) 1 else 0

            return SpacingBuilder(settings, NbttLanguage)
                .before(NbttTypes.COLON).spacing(spacesBeforeColon, spacesBeforeColon, 0, false, 0)
                .after(NbttTypes.COLON).spacing(spacesAfterColon, spacesAfterColon, 0, false, 0)
                // Empty blocks
                .between(NbttTypes.LBRACKET, NbttTypes.RBRACKET).spacing(0, 0, 0, false, 0)
                .between(NbttTypes.LPAREN, NbttTypes.RPAREN).spacing(0, 0, 0, false, 0)
                .between(NbttTypes.LBRACE, NbttTypes.RBRACE).spacing(0, 0, 0, false, 0)
                // Non-empty blocks
                .withinPair(NbttTypes.LBRACKET, NbttTypes.RBRACKET).spaceIf(commonSettings.SPACE_WITHIN_BRACKETS, true)
                .withinPair(NbttTypes.LPAREN, NbttTypes.RPAREN).spaceIf(commonSettings.SPACE_WITHIN_PARENTHESES, true)
                .withinPair(NbttTypes.LBRACE, NbttTypes.RBRACE).spaceIf(commonSettings.SPACE_WITHIN_BRACES, true)
                .before(NbttTypes.COMMA).spacing(spacesBeforeComma, spacesBeforeComma, 0, false, 0)
                .after(NbttTypes.COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA)
        }
    }
}
