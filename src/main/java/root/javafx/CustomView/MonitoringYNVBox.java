package root.javafx.CustomView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import root.core.domain.MonitoringYN;
import root.core.domain.MonitoringYN.MonitoringTypeAndYN;
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
			ToggleGroupVBox toggleGroupVBox = new ToggleGroupVBox();
			toggleGroupVBox.setParentToggle(text);

			toggleGroupVBoxs.put(type, toggleGroupVBox);
			getChildren().add(toggleGroupVBox);
		}
	}

	/**
	 * �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param type       ����͸� Ÿ��
	 * @param text       ��� ���� �� �ؽ�Ʈ
	 * @param isSelected ��� ���� ����
	 */
	public void addChildToggle(MonitoringType type, String text) {
		toggleGroupVBoxs.get(type).addChildToggle(text);
	}

	/**
	 * �ڽ� ����� ���ÿ��θ� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param monitoringYn
	 */
	public void initSelection(MonitoringYN monitoringYn) {
		String alias = monitoringYn.getMonitoringAlias();

		for (MonitoringTypeAndYN typeAndYn : monitoringYn.getMonitoringTypeList()) {
			ToggleGroupVBox toggleGroupVBox = toggleGroupVBoxs.get(typeAndYn.getMonitoringType());
			if (toggleGroupVBox != null) {
				toggleGroupVBoxs.get(typeAndYn.getMonitoringType()).setSelected(alias, typeAndYn.isMonitoring());
			}
		}
	}

	/**
	 * �ڽ� ����� ���ÿ��θ� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param list
	 */
	public void initSelection(List<MonitoringYN> list) {
		for (MonitoringYN yn : list) {
			String alias = yn.getMonitoringAlias();

			for (MonitoringTypeAndYN typeAndYn : yn.getMonitoringTypeList()) {
				if (toggleGroupVBoxs.get(typeAndYn.getMonitoringType()) != null) {
					toggleGroupVBoxs.get(typeAndYn.getMonitoringType()).setSelected(alias, typeAndYn.isMonitoring());
				}
			}
		}
	}

	public Map<MonitoringType, Map<String, Boolean>> getToggleSelection() {
		Map<MonitoringType, Map<String, Boolean>> result = new HashMap<>();

		for (MonitoringType type : toggleGroupVBoxs.keySet()) {
			ToggleGroupVBox toggleGroup = toggleGroupVBoxs.get(type);
			result.put(type, toggleGroup.getChildSelection());
		}

		return result;
	}
}
