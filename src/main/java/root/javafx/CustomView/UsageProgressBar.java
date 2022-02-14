package root.javafx.CustomView;

import javafx.scene.control.ProgressBar;
import lombok.Getter;

public class UsageProgressBar extends ProgressBar {

	@Getter
	public enum ProgressBarStatus {
		NORMAL("cornflowerblue"), DANGEROUS("#ff4141");

		private String color;

		ProgressBarStatus(String color) {
			this.color = color;
		}
	}

	public UsageProgressBar(double progress, double baseline) {
		super(progress);
		setBarColor(progress >= baseline ? ProgressBarStatus.DANGEROUS : ProgressBarStatus.NORMAL);
		getStylesheets().add(getClass().getResource("/css/usageProgressBar.css").toExternalForm());
	}

	private void setBarColor(ProgressBarStatus type) {
		setStyle("-fx-accent2:" + type.getColor());
	}
}
