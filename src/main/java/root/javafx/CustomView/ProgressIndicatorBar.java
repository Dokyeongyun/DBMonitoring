package root.javafx.CustomView;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class ProgressIndicatorBar extends StackPane {

	private static int DEFAULT_LABEL_PADDING = 5;

	private ProgressBar progressBar;

	private Text text = new Text();

	public ProgressIndicatorBar(double progress, double baseline) {
		if (progress == -1) {
			text.setText("ERROR");
			progressBar = new UsageProgressBar(ProgressBar.INDETERMINATE_PROGRESS, baseline);
		} else {
			text.setText(progress + "%");
			progressBar = new UsageProgressBar(progress / 100.0, baseline / 100.0);
		}

		progressBar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
		progressBar.setMinWidth(text.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
		progressBar.setMaxWidth(Double.MAX_VALUE);

		getChildren().setAll(progressBar, text);
	}
}