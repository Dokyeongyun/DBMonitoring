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
	 * Toggle ��ư ���� Label�� �ؽ�Ʈ�� �����Ѵ�.
	 * 
	 * @param text
	 */
	public void setLabelText(String text) {
		label.setText(text);
	}

	/**
	 * Toggle ��ư ���� Label�� �ؽ�Ʈ�� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public String getLabelText() {
		return label.getText();
	}

	/**
	 * Toggle�� �Ӽ��� �����Ѵ�.
	 * 
	 * @param size      ��ư ũ��
	 * @param color     ��ư ä�� ��
	 * @param lineColor ��ư ���� ��
	 */
	public void setToggle(int size, Paint color, Paint lineColor) {
		toggleBtn.setSize(size);
		toggleBtn.setToggleColor(color);
		toggleBtn.setToggleLineColor(lineColor);
	}

	/**
	 * Toggle ��ư�� ���ÿ��θ� ��ȯ�Ѵ�.
	 * 
	 * @param isSelected
	 */
	public void setToggleSelected(boolean isSelected) {
		toggleBtn.setSelected(isSelected);
	}

	/**
	 * Toggle ��ư�� ���õǾ����� ���θ� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public boolean isToggleSelected() {
		return toggleBtn.isSelected();
	}

	/**
	 * Toggle ��ư Ŭ�� �̺�Ʈ�� �����Ѵ�.
	 * 
	 * @param e
	 */
	public void setToggleChangeListener(ChangeListener<? super Boolean> listener) {
		toggleBtn.selectedProperty().addListener(listener);
	}
}
