package root.javafx.CustomView.UsageUI;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import root.core.domain.enums.UsageStatus;

public class UsageBarUI extends AnchorPane implements UsageUI {

	@FXML
	ProgressBar usageUI;

	@FXML
	Label usageLB;

	private double usage;

	private double baseline;

	public UsageBarUI(double usage, double baseline) {
		this.usage = usage;
		this.baseline = baseline;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usageUI/UsageBarUI.fxml"));
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
		usageUI.setProgress(usage == -1 ? ProgressBar.INDETERMINATE_PROGRESS : usage / 100.0);
	}

	@Override
	public void setColor() {
		UsageStatus type = usage >= baseline ? UsageStatus.DANGEROUS : UsageStatus.NORMAL;
		usageUI.setStyle("-fx-accent2:" + type.getColor());
	}
}