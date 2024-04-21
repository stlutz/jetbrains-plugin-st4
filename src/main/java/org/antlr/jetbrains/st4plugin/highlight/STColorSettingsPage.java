package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.antlr.jetbrains.st4plugin.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class STColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Template Name", STGroupSyntaxHighlighter.TEMPLATE_NAME),
            new AttributesDescriptor("Template Parameter", STGroupSemanticHighlightAnnotator.TEMPLATE_PARAM),
            new AttributesDescriptor("String", STGroupSyntaxHighlighter.STRING),
            new AttributesDescriptor("Keyword", STGroupSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Delimiter", STGroupSyntaxHighlighter.DELIMITER),
            new AttributesDescriptor("Line Comment", STGroupSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block Comment", STGroupSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Option", STSemanticHighlightAnnotator.OPTION),
            new AttributesDescriptor("Member", STSemanticHighlightAnnotator.MEMBER),
            new AttributesDescriptor("Expression Tag", STSemanticHighlightAnnotator.EXPR_TAG),
            new AttributesDescriptor("If Tag", STSemanticHighlightAnnotator.IF_TAG)
    };

    @Override
    public @Nullable Icon getIcon() {
        return Icons.STG_FILE;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new STGroupSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return """
                <comment>/**
                 * Multi line comment
                 */</comment>

                <keyword>delimiters</keyword> "$", "$"

                // single line comment
                myMap ::= [
                    "key": "value",
                    <keyword>default</keyword>: <keyword>key</keyword>
                ]

                myTemplate(<param>param1</param>, <param>param2</param>) ::= <<
                    hello, world
                    <comment><! some comment !></comment>
                    <iftag><delimiter><</delimiter><keyword>if</keyword> (<param>param1</param>.<member>member</member>)<delimiter>></delimiter></iftag>
                        a
                    <iftag><delimiter><</delimiter><keyword>elseif</keyword> (<keyword>true</keyword>)<delimiter>></delimiter></iftag>
                        b
                    <iftag><delimiter><</delimiter><keyword>else</keyword><delimiter>></delimiter></iftag>
                        <exprtag><delimiter><</delimiter><param>param2</param> <option>separator</option>=", "<delimiter>></delimiter></exprtag>
                    <iftag><delimiter><</delimiter><keyword>endif</keyword><delimiter>></delimiter></iftag>
                >>

                oneLiner(<param>x</param>) ::= "hello, <delimiter><</delimiter><param>x</param><delimiter>></delimiter>"
                """;
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> tagToDescriptor = new HashMap<>();
        tagToDescriptor.put("param", STGroupSemanticHighlightAnnotator.TEMPLATE_PARAM);
        tagToDescriptor.put("keyword", STGroupSyntaxHighlighter.KEYWORD);
        tagToDescriptor.put("option", STSemanticHighlightAnnotator.OPTION);
        tagToDescriptor.put("comment", STGroupSyntaxHighlighter.BLOCK_COMMENT);
        tagToDescriptor.put("delimiter", STGroupSyntaxHighlighter.DELIMITER);
        tagToDescriptor.put("member", STSemanticHighlightAnnotator.MEMBER);
        tagToDescriptor.put("iftag", STSemanticHighlightAnnotator.IF_TAG);
        tagToDescriptor.put("exprtag", STSemanticHighlightAnnotator.EXPR_TAG);
        return tagToDescriptor;
    }

    @Override
    public @NotNull AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public @NotNull ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull @Nls(
            capitalization = Nls.Capitalization.Title
    ) String getDisplayName() {
        return "StringTemplate";
    }
}
