/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.translations

import com.demonwav.mcdev.framework.CommenterTest
import com.demonwav.mcdev.framework.EdtInterceptor
import com.demonwav.mcdev.framework.ProjectBuilder
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(EdtInterceptor::class)
@DisplayName("Minecraft Lang Commenter Tests")
class LangCommenterTest : CommenterTest() {
    private fun doTest(@Language("MCLang") before: String, @Language("MCLang") after: String) {
        doTest(before, after, ".lang", ProjectBuilder::lang)
    }

    @Test
    @DisplayName("Single Line Comment Test")
    fun singleLineCommentTest() = doTest(
        """
        test.<caret>key1=value1
        test.key2=value2
        """,
        """
        #test.key1=value1
        test.k<caret>ey2=value2
        """
    )

    @Test
    @DisplayName("Multi Line Comment Test")
    fun multiLineCommentTest() = doTest(
        """
        test.key1=value1
        test.<selection>key2=value2
        test</selection>.key3=value3
        test.key4=value4
        """,
        """
        test.key1=value1
        #test.<selection>key2=value2
        #test</selection>.key3=value3
        test.key4=value4
        """
    )

    @Test
    @DisplayName("Single Line Uncomment Test")
    fun singleLineUncommentTest() = doTest(
        """
        test.key1=value1
        test.key2=value2
        #test<caret>.key3=value3
        #test.key4=value4
        """,
        """
        test.key1=value1
        test.key2=value2
        test.key3=value3
        #tes<caret>t.key4=value4
        """
    )

    @Test
    @DisplayName("Multi Line Uncomment Test")
    fun multiLineUncommentTest() = doTest(
        """
        #test.<selection>key1=value1
        #test.key2=</selection>value2
        #test.key3=value3
        test.key4=value4
        """,
        """
        test.<selection>key1=value1
        test.key2=</selection>value2
        #test.key3=value3
        test.key4=value4
        """
    )
}
