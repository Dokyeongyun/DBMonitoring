package root.javafx.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.jfoenix.controls.JFXComboBox;

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
import root.core.domain.MonitoringYN;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.domain.MonitoringYN.MonitoringTypeAndYN;
import root.core.domain.enums.MonitoringType;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.javafx.CustomView.CustomTreeTableView;
import root.javafx.CustomView.CustomTreeView;
import root.javafx.CustomView.MonitoringTableView;

public class RunMenuController implements Initializable {

	/* Dependency Injection */
	PropertyService propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());

	@FXML
	AnchorPane connInfoSettingAP;

	@FXML
	JFXComboBox<String> connInfoFileListComboBox;

	@FXML
	AnchorPane presetSettingAP;

	@FXML
	JFXComboBox<String> presetFileListComboBox;

	@FXML
	AnchorPane dbPresetAP;

	@FXML
	AnchorPane serverPresetAP;

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

		/* 1. 모니터링 접속정보 설정 */
		// 1-1. 모니터링 접속정보 설정파일 콤보박스 아이템 설정
		List<String> connInfoFileList = propService.getConnectionInfoList();
		if (connInfoFileList == null || ArrayUtils.isEmpty(connInfoFileList.toArray())) {
			// TODO 접속정보 설정파일이 없는 경우
			addMonitoringConnInfoPreview(new ArrayList<>(), new ArrayList<>());
		} else {
			connInfoFileListComboBox.getItems().addAll(connInfoFileList);
		}

		// 1-2. 모니터링 접속정보 설정파일 콤보박스 아이템 변경 리스너 설정
		connInfoFileListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			propService.loadConnectionInfoConfig(newValue);
			List<String> dbNames = propService.getMonitoringDBNameList();
			List<String> serverNames = propService.getMonitoringServerNameList();
			addMonitoringConnInfoPreview(dbNames, serverNames);
		});

		// 1-3. 모니터링 접속정보 설정파일 콤보박스 초기값 설정
		String lastUseConnInfoFile = propService.getLastUseConnectionInfoFilePath();
		if (StringUtils.isEmpty(lastUseConnInfoFile) || !connInfoFileList.contains(lastUseConnInfoFile)) {
			// 최근 사용된 접속정보 설정파일이 없거나 현재 존재하지 않는 경우, 첫 번째 설정파일 선택
			connInfoFileListComboBox.getSelectionModel().selectFirst();
		} else {
			connInfoFileListComboBox.getSelectionModel().select(lastUseConnInfoFile);
		}

		List<MonitoringYN> list = new ArrayList<>();
		List<MonitoringTypeAndYN> childList = new ArrayList<>();
		childList.add(new MonitoringTypeAndYN(MonitoringType.ARCHIVE, true));
		childList.add(new MonitoringTypeAndYN(MonitoringType.TABLE_SPACE, true));
		childList.add(new MonitoringTypeAndYN(MonitoringType.ASM_DISK, true));
		list.add(new MonitoringYN("DB1", childList));
		
		List<MonitoringYN> list2 = new ArrayList<>();
		List<MonitoringTypeAndYN> childList2 = new ArrayList<>();
		childList2.add(new MonitoringTypeAndYN(MonitoringType.OS_DISK, false));
		childList2.add(new MonitoringTypeAndYN(MonitoringType.ALERT_LOG, false));
		list2.add(new MonitoringYN("SERVER1", childList2));
		addMonitoringPresetPreview(list, list2);

		/* 2. 모니터링 여부 설정 */
		// 2-1. 모니터링 여부 Preset 콤보박스 아이템 설정
		String curConnInfoFile = connInfoFileListComboBox.getSelectionModel().getSelectedItem();
		propService.loadMonitoringInfoConfig(curConnInfoFile);
		List<String> presetFileList = propService.getMonitoringPresetNameList();
		if (presetFileList == null || presetFileList.size() == 0) {
			// TODO 모니터링 여부 Preset 설정파일이 없는 경우
			addMonitoringPresetPreview(list, list2);
		} else {
			presetFileListComboBox.getItems().addAll(presetFileList);
		}	

		// 2-2. 모니터링 여부 Preset 콤보박스 아이템 변경 리스너 설정
		presetFileListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(newValue);
			System.out.println(propService.getMonitoringPresetFilePath(newValue));
//			List<DBMonitoringYN> dbPresets = propService.getMonitoringDBNameList();
//			List<ServerMonitoringYN> serverPresets = propService.getMonitoringServerNameList();
//			addMonitoringPresetPreview(dbPresets, serverPresets);
		});

		// 2-3. 모니터링 여부 Preset 콤보박스 초기값 설정
		String lastUsePresetFileName = propService.getLastUsePresetFileName(curConnInfoFile);
		if (StringUtils.isEmpty(lastUsePresetFileName) || !presetFileList.contains(lastUsePresetFileName)) {
			// 최근 사용된 모니터링 여부 Preset 설정파일이 없거나 현재 존재하지 않는 경우, 첫 번째 설정파일 선택
			presetFileListComboBox.getSelectionModel().selectFirst();
		} else {
			presetFileListComboBox.getSelectionModel().select(lastUsePresetFileName);
		}

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

	/**
	 * 설정된 모니터링 접속정보를 보여주는 TreeView를 생성 및 추가한다.
	 * 
	 * @param dbNameList
	 * @param serverNameList
	 */
	private void addMonitoringConnInfoPreview(List<String> dbNameList, List<String> serverNameList) {
		// 접속정보 리스트 TreeView
		CustomTreeView connInfoCtv = new CustomTreeView("접속정보 리스트", FontAwesomeIcon.LIST, true);
		connInfoCtv.addTreeItem("DB", dbNameList, FontAwesomeIcon.DATABASE);
		connInfoCtv.addTreeItem("Server", serverNameList, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(connInfoCtv, 80, 0, 0, 0);
		connInfoSettingAP.getChildren().add(connInfoCtv);
	}

	/**
	 * 설정된 모니터링 여부 Preset을 보여주는 TreeTableView를 생성 및 추가한다.
	 * 
	 * @param monitoringYNList
	 */
	private void addMonitoringPresetPreview(List<MonitoringYN> dbYnList, List<MonitoringYN> serverYnList) {

		Set<MonitoringType> dbMonitoringTypeList = new HashSet<>();
		dbYnList.stream().map(m -> m.getDistinctMonitoringTypes()).collect(Collectors.toList())
				.forEach(type -> dbMonitoringTypeList.addAll(type));

		Set<MonitoringType> serverMonitoringTypeList = new HashSet<>();
		serverYnList.stream().map(m -> m.getDistinctMonitoringTypes()).collect(Collectors.toList())
				.forEach(type -> serverMonitoringTypeList.addAll(type));

		// 모니터링 여부 리스트 TreeTableView - DB
		CustomTreeTableView dbCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		dbCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		dbMonitoringTypeList.forEach(type -> dbCtv.addMonitoringYNTableColumn(type.getName(), type));
		dbCtv.addTreeTableItem(new MonitoringYN("DB"), dbYnList, FontAwesomeIcon.DATABASE);
		setAnchorPaneAnchor(dbCtv, 0, 0, 0, 0);
		dbPresetAP.getChildren().add(dbCtv);

		// 모니터링 여부 리스트 TreeTableView - Server
		CustomTreeTableView serverCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		serverCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		serverMonitoringTypeList.forEach(type -> serverCtv.addMonitoringYNTableColumn(type.getName(), type));
		serverCtv.addTreeTableItem(new MonitoringYN("Server"), serverYnList, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(serverCtv, 0, 0, 0, 0);
		serverPresetAP.getChildren().add(serverCtv);
	}
}
