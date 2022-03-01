package root.javafx.CustomView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import root.core.domain.ASMDiskUsage;
import root.core.domain.AlertLog;
import root.core.domain.ArchiveUsage;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.javafx.DI.DependencyInjection;

public class MonitoringYNVBox extends VBox {

	private static final Paint PARENT_TOGGLE_COLOR = Paint.valueOf("#0132ac");
	private static final Paint PARENT_TOGGLE_LINE_COLOR = Paint.valueOf("#6e93ea");
	private static final Paint CHILD_TOGGLE_COLOR = Paint.valueOf("#009688");
	private static final Paint CHILD_TOGGLE_LINE_COLOR = Paint.valueOf("#77c2bb");
	private static final int PARENT_TOGGLE_SIZE = 6;
	private static final int CHILD_TOGGLE_SIZE = 4;

	@FXML
	HBox archiveUsageParentHBox;

	@FXML
	FlowPane archiveUsageChildFlowPane;

	@FXML
	HBox tableSpaceUsageParentHBox;

	@FXML
	FlowPane tableSpaceUsageChildFlowPane;

	@FXML
	HBox asmDiskUsageParentHBox;

	@FXML
	FlowPane asmDiskUsageChildFlowPane;

	@FXML
	HBox osDiskUsageParentHBox;

	@FXML
	FlowPane osDiskUsageChildFlowPane;

	@FXML
	HBox alertLogParentHBox;

	@FXML
	FlowPane alertLogChildFlowPane;

	Map<Class<?>, HBox> parentHBoxMap = new HashMap<>();

	Map<Class<?>, FlowPane> childFlowPaneMap = new HashMap<>();

	Map<Class<?>, ToggleHBox> parentToggleMap = new HashMap<>();

	Map<Class<?>, List<ToggleHBox>> childToggleListMap = new HashMap<>();

	public MonitoringYNVBox() {
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/MonitoringYNVBox.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		parentHBoxMap.put(ArchiveUsage.class, archiveUsageParentHBox);
		parentHBoxMap.put(TableSpaceUsage.class, tableSpaceUsageParentHBox);
		parentHBoxMap.put(ASMDiskUsage.class, asmDiskUsageParentHBox);
		parentHBoxMap.put(OSDiskUsage.class, osDiskUsageParentHBox);
		parentHBoxMap.put(AlertLog.class, alertLogParentHBox);

		childFlowPaneMap.put(ArchiveUsage.class, archiveUsageChildFlowPane);
		childFlowPaneMap.put(TableSpaceUsage.class, tableSpaceUsageChildFlowPane);
		childFlowPaneMap.put(ASMDiskUsage.class, asmDiskUsageChildFlowPane);
		childFlowPaneMap.put(OSDiskUsage.class, osDiskUsageChildFlowPane);
		childFlowPaneMap.put(AlertLog.class, alertLogChildFlowPane);
	}

	/**
	 * �θ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param type ����͸� Ÿ��
	 * @param text ��� ��� �� �ؽ�Ʈ
	 */
	public void addParentToggle(Class<?> type, String text) {
		if (!parentToggleMap.containsKey(type)) {
			parentToggleMap.put(type, new ToggleHBox());
			childToggleListMap.put(type, new ArrayList<>());
		}

		ToggleHBox parentToggle = parentToggleMap.get(type);
		parentToggle.setLabelText(text);
		parentToggle.setToggle(PARENT_TOGGLE_SIZE, PARENT_TOGGLE_COLOR, PARENT_TOGGLE_LINE_COLOR);
		parentToggle.setToggleSelected(false);
		parentToggle.setToggleAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				for (ToggleHBox t : childToggleListMap.get(type)) {
					t.setToggleSelected(parentToggle.isToggleSelected());
				}
			}
		});

		parentHBoxMap.get(type).getChildren().add(parentToggle);
	}

	/**
	 * �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param type       ����͸� Ÿ��
	 * @param text       ��� ���� �� �ؽ�Ʈ
	 * @param isSelected ��� ���� ����
	 */
	public void addChildToggle(Class<?> type, String text, boolean isSelected) {
		addChildToggle(type, text, CHILD_TOGGLE_COLOR, CHILD_TOGGLE_LINE_COLOR, isSelected);
	}

	/**
	 * �ڽ� ��� ��ư�� �߰��Ѵ�.
	 * 
	 * @param type       ����͸� Ÿ��
	 * @param text       ��� ���� �� �ؽ�Ʈ
	 * @param color      ��� ��
	 * @param lineColor  ��� ���� ��
	 * @param isSelected ��� ���� ����
	 */
	public void addChildToggle(Class<?> type, String text, Paint color, Paint lineColor, boolean isSelected) {
		ToggleHBox toggleHBox = new ToggleHBox();
		toggleHBox.setLabelText(text);
		toggleHBox.setToggle(CHILD_TOGGLE_SIZE, color, lineColor);
		toggleHBox.setToggleSelected(isSelected);
		toggleHBox.setToggleAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (toggleHBox.isToggleSelected()) {
					parentToggleMap.get(type).setToggleSelected(true);
				} else {
					parentToggleMap.get(type).setToggleSelected(!isAllNotSelected(type));
				}
			}
		});

		parentToggleMap.get(type).setToggleSelected(!isAllNotSelected(type));

		childFlowPaneMap.get(type).getChildren().add(toggleHBox);
		childToggleListMap.get(type).add(toggleHBox);
	}

	/**
	 * �ڽ� ��� ��ư�� ��� ���õ��� �ʾҴ��� ���θ� ��ȯ�Ѵ�.
	 * 
	 * @param type ����͸� Ÿ��
	 * @return
	 */
	private boolean isAllNotSelected(Class<?> type) {
		boolean isAllNotSelected = true;
		for (ToggleHBox t : childToggleListMap.get(type)) {
			if (t.isToggleSelected()) {
				isAllNotSelected = false;
				break;
			}
		}
		return isAllNotSelected;
	}
}
