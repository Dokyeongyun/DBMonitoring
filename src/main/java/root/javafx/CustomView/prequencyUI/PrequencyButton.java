package root.javafx.CustomView.prequencyUI;

import java.util.List;

import javafx.scene.control.Button;

public class PrequencyButton extends Button {

	public PrequencyButton(List<String> monitoringTimeList) {
		
		int prequency = monitoringTimeList.size();

		String backgroundColor = prequency == 0 ? "white" : "#789bff94";
		setStyle(String.format(
				"-fx-background-color: %s; "
				+ "-fx-background-radius:0px; "
				+ "-fx-border-width: 0.2px; "
				+ "-fx-border-color: black;"
				+ "-fx-font-family: NanumGothic;"
				+ "-fx-font-size: 10px;",
				backgroundColor));
		
		setWidth(USE_PREF_SIZE);
		setMinWidth(USE_PREF_SIZE);
		setMaxWidth(USE_PREF_SIZE);
		setPrefWidth(25);

		setHeight(USE_PREF_SIZE);
		setMinHeight(USE_PREF_SIZE);
		setMaxHeight(USE_PREF_SIZE);
		setPrefHeight(25);

		setText(prequency == 0 ? "-" : String.valueOf(prequency));

		setTooltip(new PrequencyTooltip(monitoringTimeList));
	}
}
