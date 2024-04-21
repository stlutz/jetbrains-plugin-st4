package org.antlr.jetbrains.st4plugin.highlight;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.antlr.jetbrains.st4plugin.parsing.STLexer;
import org.antlr.jetbrains.st4plugin.parsing.STParser;
import org.antlr.jetbrains.st4plugin.psi.STTokenTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getRuleElementType;
import static org.antlr.jetbrains.st4plugin.psi.STTokenTypes.getTokenElementType;

/**
 * Semantic highlighting for .st files.
 */
public class STSemanticHighlightAnnotator implements Annotator {

    public static final TextAttributesKey EXPR_TAG = createTextAttributesKey("ST_EXPR_TAG");
    public static final TextAttributesKey IF_TAG = createTextAttributesKey("ST_IF_TAG");
    public static final TextAttributesKey OPTION = createTextAttributesKey("ST_OPTION", DefaultLanguageHighlighterColors.PARAMETER);
    public static final TextAttributesKey MEMBER = createTextAttributesKey("ST_MEMBER", DefaultLanguageHighlighterColors.INSTANCE_METHOD);

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getNode().getElementType() == STTokenTypes.getTokenElementType(STLexer.ID)) {
            if (isPrimary(element) || isSubtemplate(element)) {
                holder
                        .newAnnotation(HighlightSeverity.INFORMATION, "")
                        .range(element)
                        .textAttributes(STGroupSemanticHighlightAnnotator.TEMPLATE_PARAM)
                        .create();
            } else if (isMember(element)) {
                holder
                        .newAnnotation(HighlightSeverity.INFORMATION, "")
                        .range(element)
                        .textAttributes(MEMBER)
                        .create();
            } else if (isOption(element)) {
                holder
                        .newAnnotation(HighlightSeverity.INFORMATION, "")
                        .range(element)
                        .textAttributes(OPTION)
                        .create();
            } else if (isCall(element)) {
                holder
                        .newAnnotation(HighlightSeverity.INFORMATION, "")
                        .range(element)
                        .textAttributes(STGroupSyntaxHighlighter.TEMPLATE_NAME)
                        .create();
            }
        }

        if (isExprTag(element)) {
            holder
                    .newAnnotation(HighlightSeverity.INFORMATION, "")
                    .range(element)
                    .textAttributes(EXPR_TAG)
                    .create();
        }

        if (element.getNode().getElementType() == getRuleElementType(STParser.RULE_ifstat)) {
            for (ASTNode ldelim : element.getNode().getChildren(TokenSet.create(getTokenElementType(STLexer.LDELIM)))) {
                ASTNode rdelim = element.getNode().findChildByType(getTokenElementType(STLexer.RDELIM), ldelim);

                if (rdelim != null) {
                    holder
                            .newAnnotation(HighlightSeverity.INFORMATION, "")
                            .range(TextRange.create(ldelim.getStartOffset(), rdelim.getStartOffset() + rdelim.getTextLength()))
                            .textAttributes(IF_TAG)
                            .create();
                }
            }
        }
    }

    private boolean isSubtemplate(@NotNull PsiElement element) {
        return matchesRule(element.getParent(), STParser.RULE_subtemplate);
    }

    private boolean isExprTag(@NotNull PsiElement element) {
        return matchesRule(element, STParser.RULE_exprTag);
    }

    private boolean isPrimary(@NotNull PsiElement element) {
        return matchesRule(element.getParent(), STParser.RULE_primary);
    }

    private boolean isMember(@NotNull PsiElement element) {
        return matchesRule(element.getParent(), STParser.RULE_memberExpr);
    }

    private boolean isCall(PsiElement element) {
        PsiElement nextVisibleLeaf = PsiTreeUtil.nextVisibleLeaf(element);

        return nextVisibleLeaf != null && matchesToken(nextVisibleLeaf, STLexer.LPAREN);
    }

    private boolean isOption(@NotNull PsiElement element) {
        return matchesRule(element.getParent(), STParser.RULE_option);
    }

    private boolean matchesRule(@NotNull PsiElement element, int rule) {
        return element.getNode().getElementType() == STTokenTypes.getRuleElementType(rule);
    }

    private boolean matchesToken(@NotNull PsiElement element, int token) {
        return element.getNode().getElementType() == STTokenTypes.getTokenElementType(token);
    }
}
