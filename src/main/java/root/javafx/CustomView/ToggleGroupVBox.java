package root.javafx.CustomView;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class ToggleGroupVBox extends VBox {

	private static final Paint PARENT_TOGGLE_COLOR = Paint.valueOf("#0132ac");
	private static final Paint PARENT_TOGGLE_LINE_COLOR = Paint.valueOf("#6e93ea");
	private static final Paint CHILD_TOGGLE_COLOR = Paint.valueOf("#009688");
	private static final Paint CHILD_TOGGLE_LINE_COLOR = Paint.valueOf("#77c2bb");
	private static final int PARENT_TOGGLE_SIZE = 6;
	private static final int CHILD_TOGGLE_SIZE = 4;

	List<ToggleHBox> childToggleList = new ArrayList<>();

	private ToggleHBox parentToggleHBox = new ToggleHBox();

	private FlowPane childFlowPane = new FlowPane();

	public ToggleGroupVBox() {

		parentToggleHBox.setToggleAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				for (ToggleHBox t : childToggleList) {
					t.setToggleSelected(parentToggleHBox.isToggleSelected());
				}	
			}
		});

		getChildren().addAll(parentToggleHBox, childFlowPane);
	}

	/**
	 * Group�� �θ� ��� ��ư�� �ؽ�Ʈ, ������, �÷��� �����Ѵ�.
	 * 
	 * @param text ��� ���� �� �ؽ�Ʈ
	 */
	public void setParentToggle(String text) {
		parentToggleHBox.setLabelText(text);
		parentToggleHBox.setToggle(PARENT_TOGGLE_SIZE, PARENT_TOGGLE_COLOR, PARENT_TOGGLE_LINE_COLOR);
	}

	/**
	 * Group�� �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param text ��� ���� �� �ؽ�Ʈ
	 */
	public void addChildToggle(String text) {
		ToggleHBox childToggleHBox = new ToggleHBox();
		childToggleHBox.setLabelText(text);
		childToggleHBox.setToggle(CHILD_TOGGLE_SIZE, CHILD_TOGGLE_COLOR, CHILD_TOGGLE_LINE_COLOR);
		childToggleHBox.setToggleAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (childToggleHBox.isToggleSelected()) {
					parentToggleHBox.setToggleSelected(true);
				} else {
					parentToggleHBox.setToggleSelected(!isAllNotSelected());
				}
			}
		});

		childToggleList.add(childToggleHBox);
		childFlowPane.getChildren().add(childToggleHBox);
	}

	/**
	 * Group�� �ڽ� ��� ��ư�� ���ÿ��θ� �����Ѵ�.
	 * 
	 * @param alias      �����ϰ��� �ϴ� ��� ��ư ������ Label Text
	 * @param isSelected ���ÿ���
	 */
	public void setSelected(String alias, boolean isSelected) {
		childToggleList.stream().filter(child -> child.getLabelText().equals(alias))
				.forEach(child -> child.setToggleSelected(isSelected));
		parentToggleHBox.setToggleSelected(!isAllNotSelected());
	}

	/**
	 * Group�� �ڽ� ��� ��ư�� ��� ���õ��� �ʾҴ��� ���θ� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	private boolean isAllNotSelected() {
		boolean isAllNotSelected = true;
		for (ToggleHBox t : childToggleList) {
			if (t.isToggleSelected()) {
				isAllNotSelected = false;
				break;
			}
		}
		return isAllNotSelected;
	}
}
