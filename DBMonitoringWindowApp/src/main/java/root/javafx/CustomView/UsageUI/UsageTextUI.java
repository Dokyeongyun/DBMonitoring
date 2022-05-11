package root.javafx.CustomView.UsageUI;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import root.core.domain.enums.UsageStatus;
import root.javafx.DI.DependencyInjection;

public class UsageTextUI extends UsageUI {

	@FXML
	Label usageLB;

	private double usage;

	private double baseline;

	public UsageTextUI(double usage, double baseline) {
		this.usage = usage;
		this.baseline = baseline;

		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/usageUI/UsageTextUI.fxml");
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
		usageLB.setText(usage == -1 ? "ERROR" : usage + "%");
	}

	@Override
	public void setColor() {
		UsageStatus type = usage >= baseline ? UsageStatus.DANGEROUS : UsageStatus.NORMAL;
		usageLB.setStyle("-fx-text-fill:" + (type == UsageStatus.NORMAL ? "black" : type.getColor()));
	}
}