package root.javafx.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import root.common.database.contracts.AbstractDatabase;
import root.common.database.implement.JdbcConnectionInfo;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringYN;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.domain.enums.MonitoringType;
import root.core.domain.enums.RoundingDigits;
import root.core.domain.enums.UsageUIType;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.repository.implement.DBCheckRepositoryImpl;
import root.core.repository.implement.LinuxServerMonitoringRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.core.repository.implement.ReportFileRepo;
import root.core.service.contracts.PropertyService;
import root.core.service.implement.FilePropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.CustomView.CustomTreeTableView;
import root.javafx.CustomView.CustomTreeView;
import root.utils.UnitUtils.FileSize;

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
	JFXComboBox<FileSize> fileSizeCB;

	@FXML
	JFXComboBox<RoundingDigits> roundingDigitsCB;

	@FXML
	JFXComboBox<UsageUIType> usageUITypeCB;

	@FXML
	JFXToggleButton resultSaveToggleBtn;

	@FXML
	ScrollPane mainScrollPane;

	@FXML
	AnchorPane scrollAP;

	@FXML
	SplitPane resultSplitPane;

	@FXML
	AnchorPane step4AP;

	@FXML
	Label step4Label;

	@FXML
	HBox step3ToStep4Arrow;

	private MonitoringTableViewPagingBox dbResults;
	private MonitoringTableViewPagingBox serverResults;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/* 1. ����͸� �������� ���� + 2. ����͸� ���� ���� */
		initRunStep1();

		/* 3. ��Ÿ ���� �� ���� */
		initRunStep3();

		/* 4. ������ */
//		initRunStep4();

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

		List<MonitoringType> dbMonitoringTypeList = Arrays.asList(MonitoringType.values()).stream()
				.filter(m -> m.getCategory().equals("DB")).collect(Collectors.toList());

		List<MonitoringType> serverMonitoringTypeList = Arrays.asList(MonitoringType.values()).stream()
				.filter(m -> m.getCategory().equals("SERVER")).collect(Collectors.toList());

		// ����͸� ���� ����Ʈ TreeTableView - DB
		CustomTreeTableView dbCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		dbCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		dbMonitoringTypeList.forEach(type -> dbCtv.addMonitoringYNTableColumn(type.getName(), type));
		dbCtv.addTreeTableItem(new MonitoringYN("DB "), dbYnList, FontAwesomeIcon.DATABASE);
		setAnchorPaneAnchor(dbCtv, 0, 0, 0, 0);
		dbPresetAP.getChildren().add(dbCtv);

		// ����͸� ���� ����Ʈ TreeTableView - Server
		CustomTreeTableView serverCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		serverCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		serverMonitoringTypeList.forEach(type -> serverCtv.addMonitoringYNTableColumn(type.getName(), type));
		serverCtv.addTreeTableItem(new MonitoringYN("Server "), serverYnList, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(serverCtv, 0, 0, 0, 0);
		serverPresetAP.getChildren().add(serverCtv);
	}

	/**
	 * ����͸� ����
	 * 
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		initRunStep4();

		String connInfoConfigFilePath = connInfoFileListComboBox.getSelectionModel().getSelectedItem();
		String presetName = presetFileListComboBox.getSelectionModel().getSelectedItem();
		String presetConfigFilePath = propService.getMonitoringPresetFilePath(presetName);
		propService.loadConnectionInfoConfig(connInfoConfigFilePath);
		propService.loadMonitoringInfoConfig(presetConfigFilePath);

		UsageUIType usageUIType = usageUITypeCB.getSelectionModel().getSelectedItem();

		boolean isSave = resultSaveToggleBtn.isSelected();

		List<String> dbNames = propService.getMonitoringDBNameList();
		List<String> serverNames = propService.getMonitoringServerNameList();

		List<JdbcConnectionInfo> jdbcConnectionList = propService.getJdbcConnInfoList(dbNames);
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			AbstractDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());

			if (isSave) {
				usecase.writeCsvArchiveUsage();
				usecase.writeCsvTableSpaceUsage();
				usecase.writeCsvASMDiskUsage();
			}

			List<ArchiveUsage> archiveUsageList = usecase.getCurrentArchiveUsage();
			List<TableSpaceUsage> tableSpaceUsageList = usecase.getCurrentTableSpaceUsage();
			List<ASMDiskUsage> asmDiskUsageList = usecase.getCurrentASMDiskUsage();

			String dbName = jdbc.getJdbcDBName();
			dbResults.addMonitoringTableViewContainer(dbName, ArchiveUsage.class);
			dbResults.setMonitoringTableViewUsageUIType(dbName, ArchiveUsage.class, usageUIType);
			dbResults.setMonitoringTableViewData(dbName, ArchiveUsage.class, archiveUsageList);

			dbResults.addMonitoringTableViewContainer(dbName, TableSpaceUsage.class);
			dbResults.setMonitoringTableViewUsageUIType(dbName, TableSpaceUsage.class, usageUIType);
			dbResults.setMonitoringTableViewData(dbName, TableSpaceUsage.class, tableSpaceUsageList);

			dbResults.addMonitoringTableViewContainer(dbName, ASMDiskUsage.class);
			dbResults.setMonitoringTableViewUsageUIType(dbName, ASMDiskUsage.class, usageUIType);
			dbResults.setMonitoringTableViewData(dbName, ASMDiskUsage.class, asmDiskUsageList);
		}

		List<JschConnectionInfo> jschConnectionList = propService.getJschConnInfoList(serverNames);
		for (JschConnectionInfo jsch : jschConnectionList) {
			JschServer server = new JschServer(jsch);
			server.init();
			ServerMonitoringRepository repo = new LinuxServerMonitoringRepository(server);
			ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());

			if (isSave) {
				try {
					usecase.writeCsvOSDiskUsage();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			String serverName = jsch.getServerName();
			List<OSDiskUsage> osDiskUsageList = usecase.getCurrentOSDiskUsage();
			serverResults.addMonitoringTableViewContainer(serverName, OSDiskUsage.class);
			serverResults.setMonitoringTableViewUsageUIType(serverName, OSDiskUsage.class, usageUIType);
			serverResults.setMonitoringTableViewData(serverName, OSDiskUsage.class, osDiskUsageList);

//			AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(jsch.getAlc(),
//					DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1), DateUtils.getToday("yyyy-MM-dd"));
		}
	}

	/**
	 * 1. ����͸� �������� ���� ������ View�� �ʱ�ȭ�Ѵ�.
	 */
	private void initRunStep1() {
		// 1-0. Clear
		if (connInfoFileListComboBox.getItems().size() != 0) {
			connInfoFileListComboBox.getItems().clear();
		}

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
			initRunStep2();
		});

		// 1-3. ����͸� �������� �������� �޺��ڽ� �ʱⰪ ����
		String lastUseConnInfoFile = propService.getLastUseConnectionInfoFilePath();
		if (StringUtils.isEmpty(lastUseConnInfoFile) || !connInfoFileList.contains(lastUseConnInfoFile)) {
			// �ֱ� ���� �������� ���������� ���ų� ���� �������� �ʴ� ���, ù ��° �������� ����
			connInfoFileListComboBox.getSelectionModel().selectFirst();
		} else {
			connInfoFileListComboBox.getSelectionModel().select(lastUseConnInfoFile);
		}
	}

	/**
	 * 2. ����͸� ���� ���� ������ View�� �ʱ�ȭ�Ѵ�.
	 */
	private void initRunStep2() {
		// 2-0. Clear
		if (presetFileListComboBox.getItems().size() != 0) {
			presetFileListComboBox.getItems().clear();
		}

		// 2-1. ����͸� ���� Preset �޺��ڽ� ������ ����
		String curConnInfoFile = connInfoFileListComboBox.getSelectionModel().getSelectedItem();
		propService.loadMonitoringInfoConfig(curConnInfoFile);
		List<String> presetFileList = propService.getMonitoringPresetNameList();
		if (presetFileList == null || presetFileList.size() == 0) {
			// TODO ����͸� ���� Preset ���������� ���� ���
			addMonitoringPresetPreview(new ArrayList<>(), new ArrayList<>());
		} else {
			presetFileListComboBox.getItems().addAll(presetFileList);
		}

		// 2-2. ����͸� ���� Preset �޺��ڽ� ������ ���� ������ ����
		presetFileListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				List<MonitoringYN> dbYnList = propService.getDBMonitoringYnList(newValue);
				List<MonitoringYN> serverYnList = propService.getServerMonitoringYnList(newValue);
				addMonitoringPresetPreview(dbYnList, serverYnList);
			}
		});

		// 2-3. ����͸� ���� Preset �޺��ڽ� �ʱⰪ ����
		String lastUsePresetFileName = propService.getLastUsePresetFileName(curConnInfoFile);
		if (StringUtils.isEmpty(lastUsePresetFileName) || !presetFileList.contains(lastUsePresetFileName)) {
			// �ֱ� ���� ����͸� ���� Preset ���������� ���ų� ���� �������� �ʴ� ���, ù ��° �������� ����
			presetFileListComboBox.getSelectionModel().selectFirst();
		} else {
			presetFileListComboBox.getSelectionModel().select(lastUsePresetFileName);
		}
	}

	/**
	 * 3. ��Ÿ ���� �� ���� ������ View�� �ʱ�ȭ�Ѵ�.
	 */
	private void initRunStep3() {
		// 3-1. ��ȸ��� ���� �޺��ڽ�
		// ��ȸ��� ���� �޺��ڽ� ������ ����
		fileSizeCB.getItems().addAll(FileSize.values());
		fileSizeCB.getSelectionModel().select(propService.getDefaultFileSizeUnit());

		// 3-2. �ݿø� �ڸ��� �޺��ڽ�
		roundingDigitsCB.getItems().addAll(RoundingDigits.values());
		roundingDigitsCB.getSelectionModel().select(propService.getDefaultRoundingDigits());
		roundingDigitsCB.setConverter(new StringConverter<RoundingDigits>() {
			@Override
			public String toString(RoundingDigits digits) {
				return String.valueOf(digits.getDigits());
			}

			@Override
			public RoundingDigits fromString(String digits) {
				return RoundingDigits.find(digits);
			}
		});

		// 3-3. ��뷮 �÷� UI Ÿ��
		// ��뷮 ǥ�ù�� �޺��ڽ� ������ ����
		usageUITypeCB.getItems().addAll(UsageUIType.values());
		usageUITypeCB.getSelectionModel().select(propService.getDefaultUsageUIType());
		usageUITypeCB.setConverter(new StringConverter<UsageUIType>() {
			@Override
			public String toString(UsageUIType uiType) {
				return uiType.getName();
			}

			@Override
			public UsageUIType fromString(String string) {
				return UsageUIType.find(string);
			}
		});

		// 3-4. ����͸� ��� ���� ����
		resultSaveToggleBtn.selectedProperty().set(true);
	}

	/**
	 * 4. ������ ������ View�� �ʱ�ȭ�Ѵ�.
	 */
	private void initRunStep4() {
		dbResults = new MonitoringTableViewPagingBox("DB");
		serverResults = new MonitoringTableViewPagingBox("SERVER");

		resultSplitPane.getItems().clear();
		resultSplitPane.getItems().addAll(dbResults, serverResults);
		
		step4AP.setVisible(true);
		step4AP.setMinWidth(Control.USE_COMPUTED_SIZE);
		step4AP.setMaxWidth(Control.USE_COMPUTED_SIZE);
		step4AP.setPrefWidth(Control.USE_COMPUTED_SIZE);
		step3ToStep4Arrow.setVisible(true);
		step3ToStep4Arrow.setMinWidth(Control.USE_COMPUTED_SIZE);
		step3ToStep4Arrow.setMaxWidth(Control.USE_COMPUTED_SIZE);
		step3ToStep4Arrow.setPrefWidth(Control.USE_COMPUTED_SIZE);
	}
}
