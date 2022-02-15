package root.javafx.CustomView;

import javafx.scene.Node;
import javafx.scene.control.Label;
import root.core.domain.enums.UsageUIType;

public class UsageUIFactory {

	private UsageUIFactory() {
	}

	public static Node create(UsageUIType usageUIType, double usage, double baseline) {
		switch (usageUIType) {
		case NUMERIC:
			Label label = new Label();
			label.setText(usage + "%");
			return label;
		case GRAPHIC_BAR:
			return new ProgressIndicatorBar(usage, baseline);
		case GRAPHIC_PIE:
			return new ProgressIndicatorPie(usage);
		default:
			throw new RuntimeException(usageUIType.toString() + " UI is not defined");
		}
	}
}
