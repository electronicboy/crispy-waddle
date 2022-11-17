/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.at.completion

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement

class KindPrefixMatcher(prefix: String) : PrefixMatcher(prefix) {
    override fun prefixMatches(name: String) = true
    override fun cloneWithPrefix(prefix: String) = KindPrefixMatcher(prefix)

    override fun prefixMatches(element: LookupElement) =
        element.lookupString.contains(prefix, ignoreCase = true)
}
