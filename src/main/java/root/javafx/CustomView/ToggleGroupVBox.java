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
	 * Group의 부모 토글 버튼의 텍스트, 사이즈, 컬러를 설정한다.
	 * 
	 * @param text 토글 우측 라벨 텍스트
	 */
	public void setParentToggle(String text) {
		parentToggleHBox.setLabelText(text);
		parentToggleHBox.setToggle(PARENT_TOGGLE_SIZE, PARENT_TOGGLE_COLOR, PARENT_TOGGLE_LINE_COLOR);
	}

	/**
	 * Group의 자식 토글 버튼을 추가한다.
	 * 
	 * @param text 토글 우측 라벨 텍스트
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
	 * Group의 자식 토글 버튼의 선택여부를 변경한다.
	 * 
	 * @param alias      변경하고자 하는 토글 버튼 우측의 Label Text
	 * @param isSelected 선택여부
	 */
	public void setSelected(String alias, boolean isSelected) {
		childToggleList.stream().filter(child -> child.getLabelText().equals(alias))
				.forEach(child -> child.setToggleSelected(isSelected));
		parentToggleHBox.setToggleSelected(!isAllNotSelected());
	}

	/**
	 * Group의 자식 토글 버튼이 모두 선택되지 않았는지 여부를 반환한다.
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
