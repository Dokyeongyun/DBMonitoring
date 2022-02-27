package root.javafx.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.javafx.CustomView.CustomTreeTableView;
import root.javafx.CustomView.CustomTreeView;
import root.javafx.CustomView.MonitoringTableView;
import root.javafx.Model.MonitoringYN;

public class RunMenuController implements Initializable {

	@FXML
	AnchorPane connInfoSettingAP;

	@FXML
	AnchorPane presetSettingAP;

	@FXML
	ScrollPane mainScrollPane;

	@FXML
	AnchorPane scrollAP;

	@FXML
	SplitPane upperSplitPane;

	@FXML
	SplitPane lowerSplitPane;

	@FXML
	AnchorPane archiveAP;

	@FXML
	AnchorPane tableSpaceAP;

	@FXML
	AnchorPane asmDiskAP;

	@FXML
	AnchorPane osDiskAP;
	
	@FXML
	Label step4Label;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 접속정보 리스트 TreeView
		CustomTreeView connInfoCtv = new CustomTreeView("접속정보 리스트", FontAwesomeIcon.LIST, true);
		connInfoCtv.addTreeItem("DB", new ArrayList<>(Arrays.asList("DB1", "DB2", "DB3")), FontAwesomeIcon.DATABASE);
		connInfoCtv.addTreeItem("Server", new ArrayList<>(Arrays.asList("Server1", "Server2", "Server3", "Server4")),
				FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(connInfoCtv, 80, 0, 0, 0);
		connInfoSettingAP.getChildren().add(connInfoCtv);

		// 모니터링 여부 리스트 TreeTableView
		List<MonitoringYN> list1 = new ArrayList<>();
		list1.add(new MonitoringYN("DB1", "N", "N", "N", "N"));
		list1.add(new MonitoringYN("DB2", "N", "N", "N", "N"));
		list1.add(new MonitoringYN("DB3", "N", "N", "N", "N"));
		List<MonitoringYN> list2 = new ArrayList<>();
		list2.add(new MonitoringYN("Server1", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server2", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server3", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server4", "Y", "Y", "Y", "Y"));
		CustomTreeTableView presetCtv = new CustomTreeTableView("모니터링 여부 리스트", FontAwesomeIcon.LIST);
		presetCtv.addTreeTableItem("DB", list1, FontAwesomeIcon.DATABASE);
		presetCtv.addTreeTableItem("Server", list2, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(presetCtv, 80, 0, 0, 0);
		presetSettingAP.getChildren().add(presetCtv);

		// 실행결과 TableView 생성 및 Column 추가
		MonitoringTableView<ArchiveUsage> archiveTable = addMonitoringTableView(archiveAP, ArchiveUsage.class);
		archiveTable.addColumn("Archive", "archiveName");
		archiveTable.addColumn("사용량(%)", "usedPercent");
		
		MonitoringTableView<TableSpaceUsage> tableSpaceTable = addMonitoringTableView(tableSpaceAP,
				TableSpaceUsage.class);
		tableSpaceTable.addColumn("테이블스페이스", "tableSpaceName");
		tableSpaceTable.addColumn("사용량(%)", "usedPercent");
		
		MonitoringTableView<ASMDiskUsage> asmDiskTable = addMonitoringTableView(asmDiskAP, ASMDiskUsage.class);
		asmDiskTable.addColumn("디스크 그룹", "asmDiskGroupName");
		asmDiskTable.addColumn("디스크 타입", "asmDiskGroupType");	
		asmDiskTable.addColumn("사용량(%)", "usedPercent");
		
		MonitoringTableView<OSDiskUsage> osDiskTable = addMonitoringTableView(osDiskAP, OSDiskUsage.class);
		osDiskTable.addColumn("파일 시스템", "fileSystem");
		osDiskTable.addColumn("마운트 위치", "mountedOn");
		osDiskTable.addColumn("사용량(%)", "usedPercent");
	}

	/**
	 * 실행메뉴 ScrollPane scroll event
	 * 
	 * @param e
	 */
	public void scroll(ScrollEvent e) {
		if (e.getDeltaX() == 0 && e.getDeltaY() != 0) {
			// TODO 스크롤속도 설정할 수 있도록 수정
			double deltaY = e.getDeltaY() * 3;
			double width = mainScrollPane.getWidth();
			double vvalue = mainScrollPane.getHvalue();
			mainScrollPane.setHvalue(vvalue - deltaY / width);
		}
	}

	/**
	 * AnchorPane의 Anchor를 한 줄로 설정하기 위한 메서드
	 * 
	 * @param node   AnchorPane의 자식 노드
	 * @param top    top anchor
	 * @param right  right anchor
	 * @param bottom bottom anchor
	 * @param left   left anchor
	 */
	private void setAnchorPaneAnchor(Node node, double top, double right, double bottom, double left) {
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
		AnchorPane.setLeftAnchor(node, left);
	}

	/**
	 * 모니터링 실행 결과 TableView를 부모 AnchorPane에 추가한다.
	 * 
	 * @param <T>            모니터링 타입
	 * @param parent         부모 AnchorPane
	 * @param monitoringType 모니터링 타입 클래스
	 * @return 생성된 TableView
	 */
	private <T> MonitoringTableView<T> addMonitoringTableView(AnchorPane parent,
			Class<? extends MonitoringResult> monitoringType) {
		MonitoringTableView<T> tableView = new MonitoringTableView<>(monitoringType);
		setAnchorPaneAnchor(tableView, 20, 0, 0, 0);
		parent.getChildren().add(tableView);
		return tableView;
	}
}
