package root.javafx.CustomView;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ProgressIndicatorPie extends HBox {

	private static int DEFAULT_LABEL_PADDING = 5;

	private ProgressIndicator progressIndicator;

	public ProgressIndicatorPie(double progress) {
		setAlignment(Pos.CENTER);
		getStylesheets().add(getClass().getResource("/css/usageProgressBar.css").toExternalForm());

		Text text = new Text();
		if (progress == -1) {
			text.setText("ERROR");
			progressIndicator = new ProgressIndicator(0);
		} else {
			text.setText(progress + "%");
			progressIndicator = new ProgressIndicator(progress / 100.0);
		}

		progressIndicator.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
		progressIndicator.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);

		getChildren().addAll(progressIndicator, text);
	}
}