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
	 * 부모 토글 버튼을 추가한다.
	 * 
	 * @param type 모니터링 타입
	 * @param text 토글 우글 라벨 텍스트
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
	 * 자식 토글 버튼을 추가한다.
	 * 
	 * @param type       모니터링 타입
	 * @param text       토글 우측 라벨 텍스트
	 * @param isSelected 토글 선택 여부
	 */
	public void addChildToggle(MonitoringType type, String text, boolean isSelected) {
		toggleGroupVBoxs.get(type).addChildToggle(text, isSelected);
	}
}
