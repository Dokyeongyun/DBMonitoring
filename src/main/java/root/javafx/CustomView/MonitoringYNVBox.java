package root.javafx.CustomView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import root.javafx.DI.DependencyInjection;

public class MonitoringYNVBox extends VBox {

	private static final Paint PARENT_TOGGLE_COLOR = Paint.valueOf("#0132ac");
	private static final Paint PARENT_TOGGLE_LINE_COLOR = Paint.valueOf("#6e93ea");
	private static final Paint CHILD_TOGGLE_COLOR = Paint.valueOf("#009688");
	private static final Paint CHILD_TOGGLE_LINE_COLOR = Paint.valueOf("#77c2bb");

	@FXML
	HBox parentHBox;

	@FXML
	FlowPane childFlowPane;

	ToggleHBox parentToggle;

	List<ToggleHBox> childToggleList = new ArrayList<>();

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
	 * @param text       ��� ��� �� �ؽ�Ʈ
	 * @param size       ��� ũ��
	 * @param isSelected Ʈ�� ���� ����
	 */
	public void addParentToggle(String text, int size, boolean isSelected) {
		parentToggle = new ToggleHBox();
		parentToggle.setLabelText(text);
		parentToggle.setToggle(size, PARENT_TOGGLE_COLOR, PARENT_TOGGLE_LINE_COLOR);
		parentToggle.setToggleSelected(isSelected);
		parentToggle.setToggleAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				for (ToggleHBox t : childToggleList) {
					t.setToggleSelected(parentToggle.isToggleSelected());
				}
			}
		});

		parentHBox.getChildren().add(parentToggle);
	}

	/**
	 * �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param text       ��� ���� �� �ؽ�Ʈ
	 * @param size       ��� ũ��
	 * @param isSelected ��� ���� ����
	 */
	public void addChildToggle(String text, int size, boolean isSelected) {
		addChildToggle(text, size, CHILD_TOGGLE_COLOR, CHILD_TOGGLE_LINE_COLOR, isSelected);
	}

	/**
	 * �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param text       ��� ���� �� �ؽ�Ʈ
	 * @param size       ��� ũ��
	 * @param color      ��� ��
	 * @param lineColor  ��� ���� ��
	 * @param isSelected ��� ���� ����
	 */
	public void addChildToggle(String text, int size, Paint color, Paint lineColor, boolean isSelected) {
		ToggleHBox toggleHBox = new ToggleHBox();
		toggleHBox.setLabelText(text);
		toggleHBox.setToggle(size, color, lineColor);
		toggleHBox.setToggleSelected(isSelected);
		toggleHBox.setToggleAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {

				if (toggleHBox.isToggleSelected()) {
					parentToggle.setToggleSelected(true);
				} else {
					boolean isAllNotSelected = true;
					for (ToggleHBox t : childToggleList) {
						if (t.isToggleSelected()) {
							isAllNotSelected = false;
							break;
						}
					}
					parentToggle.setToggleSelected(!isAllNotSelected);
				}
			}
		});

		childFlowPane.getChildren().add(toggleHBox);
		childToggleList.add(toggleHBox);
	}
}
