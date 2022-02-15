package root.javafx.CustomView;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class ProgressIndicatorPie extends HBox {

	private static int DEFAULT_LABEL_PADDING = 5;

	private ProgressIndicator progressIndicator;

	public ProgressIndicatorPie(double progress) {
		setAlignment(Pos.CENTER_LEFT);
		getStylesheets().add(getClass().getResource("/css/usageProgressBar.css").toExternalForm());

		Label label = new Label();
		label.setAlignment(Pos.CENTER_RIGHT);
		if (progress == -1) {
			label.setText("ERROR");
			progressIndicator = new ProgressIndicator(0);
		} else {
			label.setText(progress + "%");
			progressIndicator = new ProgressIndicator(progress / 100.0);
		}

		progressIndicator.setMinHeight(label.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);

		getChildren().addAll(progressIndicator, label);
	}
}