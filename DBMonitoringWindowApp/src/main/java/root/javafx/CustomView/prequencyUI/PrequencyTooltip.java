package root.javafx.CustomView.prequencyUI;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import root.utils.DateUtils;

public class PrequencyTooltip extends Tooltip {

	private static final int MAX_SHOW_COUNT = 5;

	public PrequencyTooltip(List<String> monitoringTimeList) {
		setShowDelay(Duration.ZERO);

		setStyle("-fx-font-size: 10px; "
				+ "-fx-font-family: NanumGothic;"
				+ "-fx-background-color: white;");

		Collections.sort(monitoringTimeList);

		VBox vBox = new VBox();
		int loopCnt = Math.min(MAX_SHOW_COUNT, monitoringTimeList.size());

		if (loopCnt == 0) {
			Label label = new Label("모니터링 기록 없음");
			label.setStyle("-fx-text-fill: black;");
			vBox.getChildren().add(label);
		} else {
			for (int i = 0; i < loopCnt; i++) {
				String text = DateUtils.convertDateFormat("yyyyMMddHHmmss", "HH:mm:ss", monitoringTimeList.get(i),
						Locale.KOREA);
				Label label = new Label(text);
				label.setStyle("-fx-text-fill: black;");
				vBox.getChildren().add(label);
			}

			if (loopCnt != monitoringTimeList.size()) {
				Label label = new Label("...");
				label.setStyle("-fx-text-fill: black;");
				vBox.getChildren().add(label);
			}
		}
		setGraphic(vBox);
	}
}
