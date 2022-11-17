/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2022 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.nbt.lang.gen;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.demonwav.mcdev.nbt.lang.gen.psi.NbttTypes.*;
import static com.intellij.psi.TokenType.*;

%%

%{
    private final java.util.Deque<Integer> stack = new java.util.ArrayDeque<>();

    public NbttLexer() {
        this((java.io.Reader)null);
    }

    private void maybeBegin(final Integer i) {
        if (i != null) {
            yybegin(i);
        }
    }
%}

%public
%class NbttLexer
%implements FlexLexer
%function advance
%type IElementType

%s IN_BYTE_ARRAY
%s IN_INT_ARRAY
%s IN_LONG_ARRAY
%s IN_LIST
%s EXPECT_NEXT

%unicode

EOL_WS              = \n | \r | \r\n
LINE_WS             = \s
WHITE_SPACE_CHAR    = {EOL_WS} | {LINE_WS}
WHITE_SPACE         = {WHITE_SPACE_CHAR}+

BYTE_LITERAL = [+-]?\d+[bB]|true|false
SHORT_LITERAL = [+-]?\d+[sS]
INT_LITERAL = [+-]?\d+[iI]?
LONG_LITERAL = [+-]?\d+[lL]
FLOAT_LITERAL = [+-]?(\d+(\.\d*(E\d+)?)?[fF]|\d*\.\d+(E\d+)?[fF]|Infinity[fF])|NaN[fF]
DOUBLE_LITERAL = [+-]?(\d*\.\d+(E\d+)?[dD]?|\d+\.\d*(E\d+)?[dD]?|\d+(E\d+)?[dD]|Infinity[dD]?)|NaN[dD]?

STRING_LITERAL = \"([^\"\\\n]|(\\[\"\\nt]))*[\"\n]
UNQUOTED_STRING_LITERAL = ([^\"\s\n:{}\[\](),\d+\-]|\\[\"\\nt])|([^\"\s\n:{}\[\](),\d+\-]|\\[\"\\nt])([^\"\\:{}\[\](),\n]|\\[\"\\nt])*[^\"\s\n:{}\[\](),]

BYTE_ARRAY_LITERAL = [+-]?\d+[bB]?|true|false
ARRAY_INT_LITEARL = [+-]?\d+[iI]

LONG_ARRAY_LITERAL = [+-]?\d+[lL]?

%%

<YYINITIAL> {
    ":"                         { return COLON; }
    "}"                         { maybeBegin(stack.pollFirst()); return RBRACE; }
    "]"                         { maybeBegin(stack.pollFirst()); return RBRACKET; }
    ")"                         { maybeBegin(stack.pollFirst()); return RPAREN; }
    "{"                         { stack.offerFirst(YYINITIAL); return LBRACE; }
    "["                         { stack.offerFirst(YYINITIAL); yybegin(IN_LIST); return LBRACKET; }
    "bytes"                     { stack.offerFirst(YYINITIAL); yybegin(IN_BYTE_ARRAY); return BYTES; }
    "ints"                      { stack.offerFirst(YYINITIAL); yybegin(IN_INT_ARRAY); return INTS; }
    "longs"                     { stack.offerFirst(YYINITIAL); yybegin(IN_LONG_ARRAY); return LONGS; }

    {BYTE_LITERAL}              { return BYTE_LITERAL; }
    {SHORT_LITERAL}             { return SHORT_LITERAL; }
    {INT_LITERAL}               { return INT_LITERAL; }
    {LONG_LITERAL}              { return LONG_LITERAL; }
    {FLOAT_LITERAL}             { return FLOAT_LITERAL; }
    {DOUBLE_LITERAL}            { return DOUBLE_LITERAL; }

    {STRING_LITERAL}            { return STRING_LITERAL; }
    {UNQUOTED_STRING_LITERAL}   { return UNQUOTED_STRING_LITERAL; }
}

<IN_BYTE_ARRAY> {
    "("                         { return LPAREN; }
    ","                         { return COMMA; }
    ")"                         { maybeBegin(stack.pollFirst()); return RPAREN; }

    {BYTE_ARRAY_LITERAL}        { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return BYTE_LITERAL; }

    // Everything below this is invalid
    // we just want to match them correctly so the parser has something to grab on to

    // Integers have to be explicitly defined as ints here, implicit numbers are bytes
    {ARRAY_INT_LITEARL}         { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return INT_LITERAL; }

    {SHORT_LITERAL}             { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return SHORT_LITERAL; }
    {LONG_LITERAL}              { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return LONG_LITERAL; }
    {FLOAT_LITERAL}             { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return FLOAT_LITERAL; }
    {DOUBLE_LITERAL}            { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return DOUBLE_LITERAL; }

    {STRING_LITERAL}            { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return STRING_LITERAL; }
    {UNQUOTED_STRING_LITERAL}   { stack.offerFirst(IN_BYTE_ARRAY); yybegin(EXPECT_NEXT); return UNQUOTED_STRING_LITERAL; }
}

<IN_INT_ARRAY> {
    "("                         { return LPAREN; }
    ","                         { return COMMA; }
    ")"                         { maybeBegin(stack.pollFirst()); return RPAREN; }

    {INT_LITERAL}               { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return INT_LITERAL; }

    // Everything below this is invalid
    // we just want to match them correctly so the parser has something to grab on to

    {BYTE_LITERAL}              { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return BYTE_LITERAL; }
    {SHORT_LITERAL}             { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return SHORT_LITERAL; }
    {LONG_LITERAL}              { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return LONG_LITERAL; }
    {FLOAT_LITERAL}             { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return FLOAT_LITERAL; }
    {DOUBLE_LITERAL}            { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return DOUBLE_LITERAL; }

    {STRING_LITERAL}            { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return STRING_LITERAL; }
    {UNQUOTED_STRING_LITERAL}   { stack.offerFirst(IN_INT_ARRAY); yybegin(EXPECT_NEXT); return UNQUOTED_STRING_LITERAL; }
}

<IN_LONG_ARRAY> {
    "("                         { return LPAREN; }
    ","                         { return COMMA; }
    ")"                         { maybeBegin(stack.pollFirst()); return RPAREN; }

    {LONG_ARRAY_LITERAL}        { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return LONG_LITERAL; }

    // Everything below this is invalid
    // we just want to match them correctly so the parser has something to grab on to

    // Integers have to be explicitly defined as ints here, implicit numbers are longs
    {ARRAY_INT_LITEARL}         { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return INT_LITERAL; }

    {BYTE_LITERAL}              { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return BYTE_LITERAL; }
    {SHORT_LITERAL}             { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return SHORT_LITERAL; }
    {FLOAT_LITERAL}             { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return FLOAT_LITERAL; }
    {DOUBLE_LITERAL}            { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return DOUBLE_LITERAL; }

    {STRING_LITERAL}            { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return STRING_LITERAL; }
    {UNQUOTED_STRING_LITERAL}   { stack.offerFirst(IN_LONG_ARRAY); yybegin(EXPECT_NEXT); return UNQUOTED_STRING_LITERAL; }
}

<IN_LIST> {
    "{"                         { stack.offerFirst(IN_LIST); stack.offerFirst(EXPECT_NEXT); yybegin(YYINITIAL); return LBRACE; }
    "["                         { stack.offerFirst(IN_LIST); stack.offerFirst(EXPECT_NEXT); yybegin(IN_LIST); return LBRACKET; }
    "bytes"                     { stack.offerFirst(IN_LIST); stack.offerFirst(EXPECT_NEXT); yybegin(IN_BYTE_ARRAY); return BYTES; }
    "ints"                      { stack.offerFirst(IN_LIST); stack.offerFirst(EXPECT_NEXT); yybegin(IN_INT_ARRAY); return INTS; }
    "longs"                     { stack.offerFirst(IN_LIST); stack.offerFirst(EXPECT_NEXT); yybegin(IN_LONG_ARRAY); return LONGS; }

    ","                         { return COMMA; }

    "]"                         { maybeBegin(stack.pollFirst()); return RBRACKET; }

    {BYTE_LITERAL}              { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return BYTE_LITERAL; }
    {SHORT_LITERAL}             { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return SHORT_LITERAL; }
    {INT_LITERAL}               { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return INT_LITERAL; }
    {LONG_LITERAL}              { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return LONG_LITERAL; }
    {FLOAT_LITERAL}             { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return FLOAT_LITERAL; }
    {DOUBLE_LITERAL}            { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return DOUBLE_LITERAL; }

    {STRING_LITERAL}            { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return STRING_LITERAL; }
    {UNQUOTED_STRING_LITERAL}   { stack.offerFirst(IN_LIST); yybegin(EXPECT_NEXT); return UNQUOTED_STRING_LITERAL; }
}

<EXPECT_NEXT> {
    ","                         { maybeBegin(stack.pollFirst()); return COMMA; }
    ")"                         { maybeBegin(stack.pollFirst()); zzMarkedPos = zzStartRead; }
    "]"                         { maybeBegin(stack.pollFirst()); zzMarkedPos = zzStartRead; }
}

{WHITE_SPACE}                   { return WHITE_SPACE; }
[^]                             { return BAD_CHARACTER; }
