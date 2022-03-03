package root.javafx.CustomView;

import java.io.IOException;

import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import root.javafx.DI.DependencyInjection;

public class ToggleHBox extends HBox {

	@FXML
	JFXToggleButton toggleBtn;

	@FXML
	Label label;

	public ToggleHBox() {
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/ToggleHBox.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Toggle 버튼 우측 Label의 텍스트를 설정한다.
	 * 
	 * @param text
	 */
	public void setLabelText(String text) {
		label.setText(text);
	}

	/**
	 * Toggle 버튼 우측 Label의 텍스트를 반환한다.
	 * 
	 * @return
	 */
	public String getLabelText() {
		return label.getText();
	}

	/**
	 * Toggle의 속성을 설정한다.
	 * 
	 * @param size      버튼 크기
	 * @param color     버튼 채움 색
	 * @param lineColor 버튼 라인 색
	 */
	public void setToggle(int size, Paint color, Paint lineColor) {
		toggleBtn.setSize(size);
		toggleBtn.setToggleColor(color);
		toggleBtn.setToggleLineColor(lineColor);
	}

	/**
	 * Toggle 버튼의 선택여부를 전환한다.
	 * 
	 * @param isSelected
	 */
	public void setToggleSelected(boolean isSelected) {
		toggleBtn.setSelected(isSelected);
	}

	/**
	 * Toggle 버튼이 선택되었는지 여부를 반환한다.
	 * 
	 * @return
	 */
	public boolean isToggleSelected() {
		return toggleBtn.isSelected();
	}

	/**
	 * Toggle 버튼 클릭 이벤트를 설정한다.
	 * 
	 * @param e
	 */
	public void setToggleChangeListener(ChangeListener<? super Boolean> listener) {
		toggleBtn.selectedProperty().addListener(listener);
	}
}
