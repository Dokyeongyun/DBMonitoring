package root.javafx.CustomView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import root.core.domain.enums.MonitoringType;
import root.javafx.DI.DependencyInjection;

public class MonitoringYNVBox extends VBox {

	Map<MonitoringType, ToggleGroupVBox> toggleGroupVBoxs = new HashMap<>();

	public MonitoringYNVBox() {
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/MonitoringYNVBox.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �θ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param type ����͸� Ÿ��
	 * @param text ��� ��� �� �ؽ�Ʈ
	 */
	public void addParentToggle(MonitoringType type, String text) {
		if (!toggleGroupVBoxs.containsKey(type)) {
			toggleGroupVBoxs.put(type, new ToggleGroupVBox());
		}

		ToggleGroupVBox parentToggle = toggleGroupVBoxs.get(type);
		parentToggle.setParentToggle(text);

		getChildren().add(parentToggle);
	}

	/**
	 * �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param type       ����͸� Ÿ��
	 * @param text       ��� ���� �� �ؽ�Ʈ
	 * @param isSelected ��� ���� ����
	 */
	public void addChildToggle(MonitoringType type, String text, boolean isSelected) {
		toggleGroupVBoxs.get(type).addChildToggle(text, isSelected);
	}
}
