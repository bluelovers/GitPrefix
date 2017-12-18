package com.github.syuchan1005.emojiprefix.commit;

import com.github.syuchan1005.emojiprefix.EmojiUtil;
import com.github.syuchan1005.emojiprefix.psi.EmojiResourceProperty;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * Created by syuchan on 2017/05/29.
 */
public class EmojiCheckinHandler extends CheckinHandler {
	private static final String NO_EMOJI = "No Emoji";

	private ButtonGroup buttonGroup = new ButtonGroup();

	private CheckinProjectPanel checkinProjectPanel = null;

	public EmojiCheckinHandler(CheckinProjectPanel checkinProjectPanel) {
		JPanel emojiPanel = new JPanel();
		emojiPanel.setLayout(new VerticalFlowLayout());
		VirtualFile emojirc = checkinProjectPanel.getProject().getBaseDir().findChild(".emojirc");
		if (emojirc == null) return;
		PsiFile psiFile = PsiManager.getInstance(checkinProjectPanel.getProject()).findFile(emojirc);
		if (psiFile == null) return;
		for (PsiElement psiElement : psiFile.getChildren()) {
			if (!(psiElement instanceof EmojiResourceProperty)) continue;
			emojiPanel.add(createEmojiButton(psiElement.getFirstChild().getText(), psiElement.getLastChild().getText(), false, buttonGroup));
		}
		emojiPanel.add(createEmojiButton(null, NO_EMOJI, true, buttonGroup));
		Splitter splitter = (Splitter) checkinProjectPanel.getComponent();
		CommitMessage commitMessage = (CommitMessage) splitter.getSecondComponent();
		JComponent component = (JComponent) commitMessage.getComponent(1);
		JBScrollPane scrollPane = new JBScrollPane(emojiPanel);
		scrollPane.setBorder(null);
		Splitter commitSplitter = new Splitter();
		commitSplitter.setFirstComponent(scrollPane);
		commitSplitter.setSecondComponent((JComponent) commitMessage.getComponent(0));
		commitMessage.add(commitSplitter, 0);
		this.checkinProjectPanel = checkinProjectPanel;
	}

	@Override
	public ReturnResult beforeCheckin() {
		if (checkinProjectPanel == null) return ReturnResult.COMMIT;
		Collections.list(buttonGroup.getElements()).stream().filter(AbstractButton::isSelected).findFirst().ifPresent(button -> {
			String emoji = ((JLabel) button.getComponent(0)).getToolTipText();
			if (emoji != null) {
				checkinProjectPanel.setCommitMessage(emoji + " " + checkinProjectPanel.getCommitMessage());
			}
		});
		return ReturnResult.COMMIT;
	}

	private static JRadioButton createEmojiButton(String emoji, String description, boolean selected, ButtonGroup buttonGroup) {
		JRadioButton radioButton = new JRadioButton("", selected);
		buttonGroup.add(radioButton);
		int space = UIManager.getIcon("RadioButton.icon").getIconWidth() + radioButton.getIconTextGap();
		JLabel iconLabel;
		if (emoji != null) {
			iconLabel = new JLabel(description, EmojiUtil.getIcon(emoji.replace(":", "")), SwingConstants.CENTER);
		} else {
			iconLabel = new JLabel(description);
		}
		iconLabel.setToolTipText(emoji);
		iconLabel.setBorder(JBUI.Borders.emptyLeft(space));
		radioButton.add(iconLabel);
		iconLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				radioButton.setSelected(true);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				radioButton.requestFocus(true);
			}
		});
		return radioButton;
	}
}
