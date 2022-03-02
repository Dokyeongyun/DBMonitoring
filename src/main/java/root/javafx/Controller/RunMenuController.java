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

		/* 1. ����͸� �������� ���� */
		// 1-1. ����͸� �������� �������� �޺��ڽ� ������ ����
		List<String> connInfoFileList = propService.getConnectionInfoList();
		if (connInfoFileList == null || ArrayUtils.isEmpty(connInfoFileList.toArray())) {
			// TODO �������� ���������� ���� ���
			addMonitoringConnInfoPreview(new ArrayList<>(), new ArrayList<>());
		} else {
			connInfoFileListComboBox.getItems().addAll(connInfoFileList);
		}

		// 1-2. ����͸� �������� �������� �޺��ڽ� ������ ���� ������ ����
		connInfoFileListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			propService.loadConnectionInfoConfig(newValue);
			List<String> dbNames = propService.getMonitoringDBNameList();
			List<String> serverNames = propService.getMonitoringServerNameList();
			addMonitoringConnInfoPreview(dbNames, serverNames);
		});

		// 1-3. ����͸� �������� �������� �޺��ڽ� �ʱⰪ ����
		String lastUseConnInfoFile = propService.getLastUseConnectionInfoFilePath();
		if (StringUtils.isEmpty(lastUseConnInfoFile) || !connInfoFileList.contains(lastUseConnInfoFile)) {
			// �ֱ� ���� �������� ���������� ���ų� ���� �������� �ʴ� ���, ù ��° �������� ����
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

		/* 2. ����͸� ���� ���� */
		// 2-1. ����͸� ���� Preset �޺��ڽ� ������ ����
		String curConnInfoFile = connInfoFileListComboBox.getSelectionModel().getSelectedItem();
		propService.loadMonitoringInfoConfig(curConnInfoFile);
		List<String> presetFileList = propService.getMonitoringPresetNameList();
		if (presetFileList == null || presetFileList.size() == 0) {
			// TODO ����͸� ���� Preset ���������� ���� ���
			addMonitoringPresetPreview(list, list2);
		} else {
			presetFileListComboBox.getItems().addAll(presetFileList);
		}	

		// 2-2. ����͸� ���� Preset �޺��ڽ� ������ ���� ������ ����
		presetFileListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(newValue);
			System.out.println(propService.getMonitoringPresetFilePath(newValue));
//			List<DBMonitoringYN> dbPresets = propService.getMonitoringDBNameList();
//			List<ServerMonitoringYN> serverPresets = propService.getMonitoringServerNameList();
//			addMonitoringPresetPreview(dbPresets, serverPresets);
		});

		// 2-3. ����͸� ���� Preset �޺��ڽ� �ʱⰪ ����
		String lastUsePresetFileName = propService.getLastUsePresetFileName(curConnInfoFile);
		if (StringUtils.isEmpty(lastUsePresetFileName) || !presetFileList.contains(lastUsePresetFileName)) {
			// �ֱ� ���� ����͸� ���� Preset ���������� ���ų� ���� �������� �ʴ� ���, ù ��° �������� ����
			presetFileListComboBox.getSelectionModel().selectFirst();
		} else {
			presetFileListComboBox.getSelectionModel().select(lastUsePresetFileName);
		}

		// ������ TableView ���� �� Column �߰�
		MonitoringTableView<ArchiveUsage> archiveTable = addMonitoringTableView(archiveAP, ArchiveUsage.class);
		archiveTable.addColumn("Archive", "archiveName");
		archiveTable.addColumn("��뷮(%)", "usedPercent");

		MonitoringTableView<TableSpaceUsage> tableSpaceTable = addMonitoringTableView(tableSpaceAP,
				TableSpaceUsage.class);
		tableSpaceTable.addColumn("���̺����̽�", "tableSpaceName");
		tableSpaceTable.addColumn("��뷮(%)", "usedPercent");

		MonitoringTableView<ASMDiskUsage> asmDiskTable = addMonitoringTableView(asmDiskAP, ASMDiskUsage.class);
		asmDiskTable.addColumn("��ũ �׷�", "asmDiskGroupName");
		asmDiskTable.addColumn("��ũ Ÿ��", "asmDiskGroupType");
		asmDiskTable.addColumn("��뷮(%)", "usedPercent");

		MonitoringTableView<OSDiskUsage> osDiskTable = addMonitoringTableView(osDiskAP, OSDiskUsage.class);
		osDiskTable.addColumn("���� �ý���", "fileSystem");
		osDiskTable.addColumn("����Ʈ ��ġ", "mountedOn");
		osDiskTable.addColumn("��뷮(%)", "usedPercent");
	}

	/**
	 * ����޴� ScrollPane scroll event
	 * 
	 * @param e
	 */
	public void scroll(ScrollEvent e) {
		if (e.getDeltaX() == 0 && e.getDeltaY() != 0) {
			// TODO ��ũ�Ѽӵ� ������ �� �ֵ��� ����
			double deltaY = e.getDeltaY() * 3;
			double width = mainScrollPane.getWidth();
			double vvalue = mainScrollPane.getHvalue();
			mainScrollPane.setHvalue(vvalue - deltaY / width);
		}
	}

	/**
	 * AnchorPane�� Anchor�� �� �ٷ� �����ϱ� ���� �޼���
	 * 
	 * @param node   AnchorPane�� �ڽ� ���
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
	 * ����͸� ���� ��� TableView�� �θ� AnchorPane�� �߰��Ѵ�.
	 * 
	 * @param <T>            ����͸� Ÿ��
	 * @param parent         �θ� AnchorPane
	 * @param monitoringType ����͸� Ÿ�� Ŭ����
	 * @return ������ TableView
	 */
	private <T> MonitoringTableView<T> addMonitoringTableView(AnchorPane parent,
			Class<? extends MonitoringResult> monitoringType) {
		MonitoringTableView<T> tableView = new MonitoringTableView<>(monitoringType);
		setAnchorPaneAnchor(tableView, 20, 0, 0, 0);
		parent.getChildren().add(tableView);
		return tableView;
	}

	/**
	 * ������ ����͸� ���������� �����ִ� TreeView�� ���� �� �߰��Ѵ�.
	 * 
	 * @param dbNameList
	 * @param serverNameList
	 */
	private void addMonitoringConnInfoPreview(List<String> dbNameList, List<String> serverNameList) {
		// �������� ����Ʈ TreeView
		CustomTreeView connInfoCtv = new CustomTreeView("�������� ����Ʈ", FontAwesomeIcon.LIST, true);
		connInfoCtv.addTreeItem("DB", dbNameList, FontAwesomeIcon.DATABASE);
		connInfoCtv.addTreeItem("Server", serverNameList, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(connInfoCtv, 80, 0, 0, 0);
		connInfoSettingAP.getChildren().add(connInfoCtv);
	}

	/**
	 * ������ ����͸� ���� Preset�� �����ִ� TreeTableView�� ���� �� �߰��Ѵ�.
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

		// ����͸� ���� ����Ʈ TreeTableView - DB
		CustomTreeTableView dbCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		dbCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		dbMonitoringTypeList.forEach(type -> dbCtv.addMonitoringYNTableColumn(type.getName(), type));
		dbCtv.addTreeTableItem(new MonitoringYN("DB"), dbYnList, FontAwesomeIcon.DATABASE);
		setAnchorPaneAnchor(dbCtv, 0, 0, 0, 0);
		dbPresetAP.getChildren().add(dbCtv);

		// ����͸� ���� ����Ʈ TreeTableView - Server
		CustomTreeTableView serverCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		serverCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		serverMonitoringTypeList.forEach(type -> serverCtv.addMonitoringYNTableColumn(type.getName(), type));
		serverCtv.addTreeTableItem(new MonitoringYN("Server"), serverYnList, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(serverCtv, 0, 0, 0, 0);
		serverPresetAP.getChildren().add(serverCtv);
	}
}
