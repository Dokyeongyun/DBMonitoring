package root.javafx.CustomView.UsageUI;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import root.core.domain.enums.UsageStatus;

public class UsageCircleUI extends UsageUI {

	@FXML
	ProgressIndicator usageUI;

	@FXML
	Label usageLB;

	private double usage;

	private double baseline;

	public UsageCircleUI(double usage, double baseline) {
		this.usage = usage;
		this.baseline = baseline;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usageUI/UsageCircleUI.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setUsage();
		setUsageText();
		setColor();
	}

	@Override
	public void setUsageText() {
		usageLB.setText(usage == -1 ? "ERROR" : usage + "%");
	}

	@Override
	public void setUsage() {
		usageUI.setProgress(usage == -1 ? 0 : usage / 100.0);
	}

	@Override
	public void setColor() {
		String color = usage >= baseline ? UsageStatus.DANGEROUS.getColor() : UsageStatus.NORMAL.getColor();
		usageUI.setStyle("-fx-progress-color: " + color);
	}
}