package com.github.syuchan1005.gitprefix.completion;

import com.github.syuchan1005.gitprefix.PrefixUtil;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import java.util.Map;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public class PrefixResourceCompletionContributor extends CompletionContributor {
	public PrefixResourceCompletionContributor() {
		extend(CompletionType.BASIC, PlatformPatterns.psiElement(PsiElement.class), new CompletionProvider<CompletionParameters>() {
			@Override
			protected void addCompletions(@NotNull CompletionParameters completionParameters,
										  ProcessingContext processingContext,
										  @NotNull CompletionResultSet completionResultSet) {
				Document document = completionParameters.getEditor().getDocument();
				int lineStart = document.getLineStartOffset(document.getLineNumber(completionParameters.getOffset()));
				String lineText = document.getText(new TextRange(lineStart, completionParameters.getOffset()));
				if (lineText.charAt(0) == ':') {
					for (Map.Entry<String, Icon> iconEntry : PrefixUtil.getEmojiMap().entrySet()) {
						completionResultSet.addElement(LookupElementBuilder.create(iconEntry.getKey(), iconEntry.getKey() + ":")
								.withIcon(iconEntry.getValue())
								.withInsertHandler((insertionContext, lookupElement) -> {
									int startOffset = insertionContext.getStartOffset();
									Document insertDocument = insertionContext.getDocument();
									if (startOffset > 0 && insertDocument.getCharsSequence().charAt(startOffset - 1) == ':') {
										insertDocument.deleteString(startOffset, startOffset);
									}
								}));
					}
				}
			}
		});
	}
}
