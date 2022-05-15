package root.javafx.Controller;

import java.io.IOException;
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
import lombok.extern.slf4j.Slf4j;
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
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.service.contracts.PropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.CustomView.CustomTreeTableView;
import root.javafx.CustomView.CustomTreeView;
import root.javafx.DI.DependencyInjection;
import root.javafx.utils.SceneUtils;
import root.repository.implement.DBCheckRepositoryImpl;
import root.repository.implement.LinuxServerMonitoringRepository;
import root.repository.implement.PropertyRepositoryImpl;
import root.repository.implement.ReportFileRepo;
import root.service.implement.FilePropertyService;
import root.utils.UnitUtils.FileSize;

@Slf4j
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
	
	@FXML
	AnchorPane noPropertyFileAP;

	private MonitoringTableViewPagingBox dbResults;
	private MonitoringTableViewPagingBox serverResults;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			/* 1. 모니터링 접속정보 설정 + 2. 모니터링 여부 설정 */
			initRunStep1();
			
			/* 3. 기타 설정 및 실행 */
			initRunStep3();

			/* 4. 실행결과 */
			// initRunStep4();

			noPropertyFileAP.setVisible(false);
		} catch (PropertyNotFoundException e) {
			log.error(e.getMessage());
			noPropertyFileAP.setVisible(true);
		}
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

		List<MonitoringType> dbMonitoringTypeList = Arrays.asList(MonitoringType.values()).stream()
				.filter(m -> m.getCategory().equals("DB")).collect(Collectors.toList());

		List<MonitoringType> serverMonitoringTypeList = Arrays.asList(MonitoringType.values()).stream()
				.filter(m -> m.getCategory().equals("SERVER")).collect(Collectors.toList());

		// 모니터링 여부 리스트 TreeTableView - DB
		CustomTreeTableView dbCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		dbCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		dbMonitoringTypeList.forEach(type -> dbCtv.addMonitoringYNTableColumn(type.getName(), type));
		dbCtv.addTreeTableItem(new MonitoringYN("DB "), dbYnList, FontAwesomeIcon.DATABASE);
		setAnchorPaneAnchor(dbCtv, 0, 0, 0, 0);
		dbPresetAP.getChildren().add(dbCtv);

		// 모니터링 여부 리스트 TreeTableView - Server
		CustomTreeTableView serverCtv = new CustomTreeTableView("", FontAwesomeIcon.LIST);
		serverCtv.addMonitoringInstanceColumn("Instance", "monitoringAlias");
		serverMonitoringTypeList.forEach(type -> serverCtv.addMonitoringYNTableColumn(type.getName(), type));
		serverCtv.addTreeTableItem(new MonitoringYN("Server "), serverYnList, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(serverCtv, 0, 0, 0, 0);
		serverPresetAP.getChildren().add(serverCtv);
	}

	/**
	 * 모니터링 실행
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

			// AlertLogCommandPeriod alcp = new AlertLogCommandPeriod(jsch.getAlc(),
			// DateUtils.addDate(DateUtils.getToday("yyyy-MM-dd"), 0, 0, -1),
			// DateUtils.getToday("yyyy-MM-dd"));
		}
	}

	/**
	 * 1. 모니터링 접속정보 설정 영역의 View를 초기화한다.
	 * @throws PropertyNotFoundException 
	 */
	private void initRunStep1() throws PropertyNotFoundException {
		// 1-0. Clear
		if (connInfoFileListComboBox.getItems().size() != 0) {
			connInfoFileListComboBox.getItems().clear();
		}

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
			initRunStep2();
		});

		// 1-3. 모니터링 접속정보 설정파일 콤보박스 초기값 설정
		String lastUseConnInfoFile = propService.getLastUseConnectionInfoFilePath();
		if (StringUtils.isEmpty(lastUseConnInfoFile) || !connInfoFileList.contains(lastUseConnInfoFile)) {
			// 최근 사용된 접속정보 설정파일이 없거나 현재 존재하지 않는 경우, 첫 번째 설정파일 선택
			connInfoFileListComboBox.getSelectionModel().selectFirst();
		} else {
			connInfoFileListComboBox.getSelectionModel().select(lastUseConnInfoFile);
		}
	}

	/**
	 * 2. 모니터링 여부 설정 영역의 View를 초기화한다.
	 */
	private void initRunStep2() {
		// 2-0. Clear
		if (presetFileListComboBox.getItems().size() != 0) {
			presetFileListComboBox.getItems().clear();
		}

		// 2-1. 모니터링 여부 Preset 콤보박스 아이템 설정
		String curConnInfoFile = connInfoFileListComboBox.getSelectionModel().getSelectedItem();
		propService.loadMonitoringInfoConfig(curConnInfoFile);
		List<String> presetFileList = propService.getMonitoringPresetNameList();
		if (presetFileList == null || presetFileList.size() == 0) {
			// TODO 모니터링 여부 Preset 설정파일이 없는 경우
			addMonitoringPresetPreview(new ArrayList<>(), new ArrayList<>());
		} else {
			presetFileListComboBox.getItems().addAll(presetFileList);
		}

		// 2-2. 모니터링 여부 Preset 콤보박스 아이템 변경 리스너 설정
		presetFileListComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				List<MonitoringYN> dbYnList = propService.getDBMonitoringYnList(newValue);
				List<MonitoringYN> serverYnList = propService.getServerMonitoringYnList(newValue);
				addMonitoringPresetPreview(dbYnList, serverYnList);
			}
		});

		// 2-3. 모니터링 여부 Preset 콤보박스 초기값 설정
		String lastUsePresetFileName = propService.getLastUsePresetFileName(curConnInfoFile);
		if (StringUtils.isEmpty(lastUsePresetFileName) || !presetFileList.contains(lastUsePresetFileName)) {
			// 최근 사용된 모니터링 여부 Preset 설정파일이 없거나 현재 존재하지 않는 경우, 첫 번째 설정파일 선택
			presetFileListComboBox.getSelectionModel().selectFirst();
		} else {
			presetFileListComboBox.getSelectionModel().select(lastUsePresetFileName);
		}
	}

	/**
	 * 3. 기타 설정 및 실행 영역의 View를 초기화한다.
	 */
	private void initRunStep3() {
		// 3-1. 조회결과 단위 콤보박스
		// 조회결과 단위 콤보박스 아이템 설정
		fileSizeCB.getItems().addAll(FileSize.values());
		fileSizeCB.getSelectionModel().select(propService.getDefaultFileSizeUnit());

		// 3-2. 반올림 자릿수 콤보박스
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

		// 3-3. 사용량 컬럼 UI 타입
		// 사용량 표시방법 콤보박스 아이템 설정
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

		// 3-4. 모니터링 결과 저장 여부
		resultSaveToggleBtn.selectedProperty().set(true);
	}

	/**
	 * 4. 실행결과 영역의 View를 초기화한다.
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
	
	/**
	 * 설정 메뉴로 이동
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goSettingMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/SettingMenu.fxml"));
	}
}
