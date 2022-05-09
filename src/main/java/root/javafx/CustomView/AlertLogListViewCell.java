package root.javafx.CustomView;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.collection.ListModification;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import root.core.domain.Log;
import root.javafx.DI.DependencyInjection;

@Slf4j
public class AlertLogListViewCell extends ListCell<Log> {
	private FXMLLoader loader;

	private Pattern KEYWORD_PATTERN;

	@FXML
	AnchorPane rootAP;
	@FXML
	Label logTimeLabel;
	@FXML
	Label logIndexLabel;
	@FXML
	FontAwesomeIconView logStatusIcon;
	@FXML
	HBox logContentHBox;

	public AlertLogListViewCell(String... highlightKeywords) {
		String join = String.join("|", highlightKeywords);
		KEYWORD_PATTERN = Pattern.compile("(?<KEYWORD1>\\b(" + join + ")\\b)"
				+ "|(?<KEYWORD2>\\b(" + join + ")\\B)"
						+ "|(?<KEYWORD3>\\B(" + join + ")\\b)"
								+ "|(?<KEYWORD4>\\B(" + join + ")\\B)");
	}

	@Override
	protected void updateItem(Log logObj, boolean empty) {
		super.updateItem(logObj, empty);

		if (empty || logObj == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (loader == null) {
				FXMLLoader loader = DependencyInjection.getLoader("/fxml/AlertLogListViewCell.fxml");
				loader.setController(this);
				try {
					loader.load();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}

			boolean isErrorLog = isErrorLog(logObj.getFullLogString());

			// logTimeStamp
			logTimeLabel.setText(logObj.getLogTimeStamp());

			// logIndex
			logIndexLabel.setText(String.valueOf(logObj.getIndex() + 1));

			// logStatusIcon
			logStatusIcon.setFill(Paint.valueOf(isErrorLog ? "#d92a2a" : "#4d9c84"));

			// logContent
			StyleClassedTextArea codeArea = new StyleClassedTextArea();
			codeArea.setPadding(new Insets(2, 5, 0, 5));
			codeArea.setWrapText(false);
			codeArea.setEditable(false);
			codeArea.autosize();
			codeArea.setFocusTraversable(true);
			HBox.setHgrow(codeArea, Priority.ALWAYS);

			String baseDir = System.getProperty("resourceBaseDir");
			codeArea.getStylesheets()
					.add(getClass().getResource(baseDir + "/css/alertLogListViewCell.css").toExternalForm());
			codeArea.getStyleClass().add("code-area");
			codeArea.getVisibleParagraphs()
					.addModificationObserver(new VisibleParagraphStyler<>(codeArea, this::computeHighlighting));

			codeArea.replaceText(0, 0, logObj.getFullLogString());
			if (isErrorLog) {
				codeArea.getStyleClass().add(".error");
			}

			// Set initial height
			Text text = new Text();
			text.setWrappingWidth(logContentHBox.widthProperty().doubleValue());
			text.setText(codeArea.getText());
			codeArea.setPrefHeight(text.getLayoutBounds().getHeight());
			logContentHBox.getChildren().addAll(codeArea);

			setText(null);
			setGraphic(rootAP);
			setStyle("-fx-padding: 0");

			// Propagate scroll event to parent listview
			codeArea.addEventFilter(ScrollEvent.ANY, scroll -> {
				codeArea.getParent().getParent().getParent().fireEvent(scroll);
				scroll.consume();
			});
		}
	}

	private boolean isErrorLog(String logContent) {
		// TODO Remove hard-coding that identifying error log
		return logContent.contains("ORA-");
	}

	private StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = KEYWORD_PATTERN.matcher(text);
		int lastKeywordEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("KEYWORD1") != null ? "keyword"
					: matcher.group("KEYWORD2") != null ? "keyword"
							: matcher.group("KEYWORD3") != null ? "keyword"
									: matcher.group("KEYWORD4") != null ? "keyword" : null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKeywordEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKeywordEnd);

		return spansBuilder.create();
	}

	private class VisibleParagraphStyler<PS, SEG, S>
			implements Consumer<ListModification<? extends Paragraph<PS, SEG, S>>> {
		private final GenericStyledArea<PS, SEG, S> area;
		private final Function<String, StyleSpans<S>> computeStyles;
		private int prevParagraph, prevTextLength;

		public VisibleParagraphStyler(GenericStyledArea<PS, SEG, S> area,
				Function<String, StyleSpans<S>> computeStyles) {
			this.computeStyles = computeStyles;
			this.area = area;
		}

		@Override
		public void accept(ListModification<? extends Paragraph<PS, SEG, S>> lm) {
			if (lm.getAddedSize() > 0) {
				int paragraph = Math.min(area.firstVisibleParToAllParIndex() + lm.getFrom(),
						area.getParagraphs().size() - 1);
				String text = area.getText(paragraph, 0, paragraph, area.getParagraphLength(paragraph));

				if (paragraph != prevParagraph || text.length() != prevTextLength) {
					int startPos = area.getAbsolutePosition(paragraph, 0);
					Platform.runLater(() -> area.setStyleSpans(startPos, computeStyles.apply(text)));
					prevTextLength = text.length();
					prevParagraph = paragraph;
				}
			}
		}
	}
}
