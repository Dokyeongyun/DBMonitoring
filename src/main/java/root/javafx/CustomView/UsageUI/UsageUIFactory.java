package root.javafx.CustomView.UsageUI;

import javafx.scene.Node;
import javafx.scene.control.Label;
import root.core.domain.enums.UsageUIType;

public class UsageUIFactory {

	private UsageUIFactory() {
	}

	public static Node create(UsageUIType usageUIType, double usage, double baseline) {
		switch (usageUIType) {
		case NUMERIC:
			return new Label(usage + "%");
		case GRAPHIC_BAR:
			return new UsageBarUI(usage, baseline);
		case GRAPHIC_PIE:
			return new UsageCircleUI(usage, baseline);
		default:
			throw new RuntimeException(usageUIType.toString() + " UI is not defined");
		}
	}
}
