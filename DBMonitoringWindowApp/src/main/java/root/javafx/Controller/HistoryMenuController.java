package root.javafx.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import root.common.database.implement.JdbcConnectionInfo;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.domain.exceptions.PropertyNotFoundException;
import root.core.repository.constracts.DBCheckRepository;
import root.core.repository.constracts.ServerMonitoringRepository;
import root.core.service.contracts.PropertyService;
import root.core.usecase.constracts.DBCheckUsecase;
import root.core.usecase.constracts.ServerMonitoringUsecase;
import root.core.usecase.implement.DBCheckUsecaseImpl;
import root.core.usecase.implement.ServerMonitoringUsecaseImpl;
import root.javafx.DI.DependencyInjection;
import root.javafx.utils.SceneUtils;
import root.repository.implement.DBCheckRepositoryImpl;
import root.repository.implement.LinuxServerMonitoringRepository;
import root.repository.implement.PropertyRepositoryImpl;
import root.repository.implement.ReportFileRepo;
import root.service.implement.FilePropertyService;

@Slf4j
public class HistoryMenuController implements Initializable {

	/* Dependency Injection */
	PropertyService propService;

	/* View Binding */
	@FXML
	JFXComboBox<String> runConnInfoFileComboBox;

	@FXML
	AnchorPane archiveUsageTabAP;

	@FXML
	AnchorPane tableSpaceUsageTabAP;

	@FXML
	AnchorPane asmDiskUsageTabAP;

	@FXML
	AnchorPane osDiskUsageTabAP;
	
	@FXML
	AnchorPane topMenuBar;
	
	@FXML
	AnchorPane noPropertyFileAP;

	/* Custom View */
	MonitoringAPController<ArchiveUsage> archiveUsageMAP;
	MonitoringAPController<TableSpaceUsage> tableSpaceUsageMAP;
	MonitoringAPController<ASMDiskUsage> asmDiskUsageMAP;
	MonitoringAPController<OSDiskUsage> osDiskUsageMAP;

	public HistoryMenuController() {
		this.propService = new FilePropertyService(PropertyRepositoryImpl.getInstance());
		this.archiveUsageMAP = new MonitoringAPController<>(ArchiveUsage.class);
		this.tableSpaceUsageMAP = new MonitoringAPController<>(TableSpaceUsage.class);
		this.asmDiskUsageMAP = new MonitoringAPController<>(ASMDiskUsage.class);
		this.osDiskUsageMAP = new MonitoringAPController<>(OSDiskUsage.class);
	}

	/**
	 * ???????????? ?????? ????????? ???????????? ????????????.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// ???????????? ?????? ???????????? ??????
		List<String> connInfoFiles;
		try {
			connInfoFiles = propService.getConnectionInfoList();
			if (connInfoFiles != null && connInfoFiles.size() != 0) {
				// Connection Info ComboBox
				runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
				runConnInfoFileComboBox.getSelectionModel().selectFirst();

				// remember.properties ????????????, ?????? ????????? ???????????? ????????? ????????? ?????? ??????????????? ????????????.
				String lastUseConnInfoFilePath = propService.getLastUseConnectionInfoFilePath();
				if (lastUseConnInfoFilePath != null) {
					runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);
				}
				
				setNoPropertyUIVisible(false);
			} else {
				setNoPropertyUIVisible(true);
				return;
			}
		} catch (PropertyNotFoundException e) {
			log.error(e.getMessage());
			setNoPropertyUIVisible(true);
			return;
		}

		// ComboBox ?????? ?????????
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					// TODO ??? Tab??? ???????????? ????????? ??????
				});

		String dbComboBoxLabel = "DB ??????";
		List<String> dbComboBoxItems = propService.getMonitoringDBNameList();
		String serverComboBoxLabel = "Server ??????";
		List<String> serverComboBoxItems = propService.getMonitoringServerNameList();

		initAndAddMonitoringAnchorPane(archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems);
	}

	/**
	 * ???????????? AnchorPane ???????????? ????????? ???????????????.
	 * 
	 * @param <T>
	 * @param monitoringAP
	 * @param parentAP
	 * @param labelText
	 * @param comboBoxItems
	 * @param tableColumns
	 */
	private <T extends MonitoringResult> void initAndAddMonitoringAnchorPane(MonitoringAPController<T> monitoringAP,
			AnchorPane parentAP, String labelText, List<String> comboBoxItems) {
		monitoringAP.setAliasComboBoxLabelText(labelText); // ComboBox ?????? Lebel Text ??????
		monitoringAP.setAliasComboBoxItems(comboBoxItems); // ComboBox Items ??????
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane??? ?????? Node??? ??????
	}

	/**
	 * [??????] - ??????????????? ????????????.
	 * 
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		// DB Usage Check
		List<JdbcConnectionInfo> jdbcConnectionList = propService
				.getJdbcConnInfoList(propService.getMonitoringDBNameList());
		for (JdbcConnectionInfo jdbc : jdbcConnectionList) {
			JdbcDatabase db = new JdbcDatabase(jdbc);
			db.init();
			DBCheckRepository repo = new DBCheckRepositoryImpl(db);
			DBCheckUsecase usecase = new DBCheckUsecaseImpl(repo, ReportFileRepo.getInstance());
			archiveUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentArchiveUsage());
			tableSpaceUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentTableSpaceUsage());
			asmDiskUsageMAP.addTableData(jdbc.getJdbcDBName(), usecase.getCurrentASMDiskUsage());
			db.uninit();
		}

		List<JschConnectionInfo> jschConnectionList = propService
				.getJschConnInfoList(propService.getMonitoringServerNameList());
		for (JschConnectionInfo jsch : jschConnectionList) {
			JschServer server = new JschServer(jsch);
			server.init();
			ServerMonitoringRepository repo = new LinuxServerMonitoringRepository(server);
			ServerMonitoringUsecase usecase = new ServerMonitoringUsecaseImpl(repo, ReportFileRepo.getInstance());

			osDiskUsageMAP.addTableData(server.getServerName(), usecase.getCurrentOSDiskUsage());
		}

		archiveUsageMAP.syncTableData(archiveUsageMAP.getSelectedAliasComboBoxItem(), 0);
		tableSpaceUsageMAP.syncTableData(tableSpaceUsageMAP.getSelectedAliasComboBoxItem(), 0);
		asmDiskUsageMAP.syncTableData(asmDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
		osDiskUsageMAP.syncTableData(osDiskUsageMAP.getSelectedAliasComboBoxItem(), 0);
	}
	
	/**
	 * ?????? ????????? ??????
	 * 
	 * @param e
	 * @throws IOException
	 */
	public void goSettingMenu(ActionEvent e) throws IOException {
		SceneUtils.movePage(DependencyInjection.load("/fxml/SettingMenu.fxml"));
	}
	
	/**
	 * ???????????? ???????????? ??????
	 * 
	 * @param e
	 */
	public void openPropertiesFile(ActionEvent e) {
		
	}
	
	/**
	 * ???????????? ?????? ????????? ?????? ??? UI Visible ??????
	 * 
	 * @param isVisible
	 */
	private void setNoPropertyUIVisible(boolean isVisible) {
		noPropertyFileAP.setVisible(isVisible);
		topMenuBar.setVisible(!isVisible);
	}
}
