package root.javafx.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import root.common.database.implement.JdbcConnectionInfo;
import root.common.database.implement.JdbcDatabase;
import root.common.server.implement.JschConnectionInfo;
import root.common.server.implement.JschServer;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
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
import root.javafx.utils.AlertUtils;

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
	 * ����޴� ȭ�� ���Խ� �ʱ�ȭ�� �����Ѵ�.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// �������� ���� ������Ƽ ����
		List<String> connInfoFiles = propService.getConnectionInfoList();
		if (connInfoFiles != null && connInfoFiles.size() != 0) {
			// Connection Info ComboBox
			runConnInfoFileComboBox.getItems().addAll(connInfoFiles);
			runConnInfoFileComboBox.getSelectionModel().selectFirst();

			// remember.properties ���Ͽ���, �ֱ� ���� �������� ��ΰ� �ִٸ� �ش� ���������� �ҷ��´�.
			String lastUseConnInfoFilePath = propService.getLastUseConnectionInfoFilePath();
			if (lastUseConnInfoFilePath != null) {
				runConnInfoFileComboBox.getSelectionModel().select(lastUseConnInfoFilePath);
			}
		} else {
			AlertUtils.showAlert(AlertType.INFORMATION, "�������� ����", "������ DB/Server ���������� �����ϴ�.\n[����]�޴��� �̵��մϴ�.");
			return;
		}

		// ComboBox ���� �̺�Ʈ
		runConnInfoFileComboBox.getSelectionModel().selectedItemProperty()
				.addListener((options, oldValue, newValue) -> {
					// TODO �� Tab�� �޺��ڽ� ������ ����
				});

		String dbComboBoxLabel = "DB ����";
		List<String> dbComboBoxItems = propService.getMonitoringDBNameList();
		String serverComboBoxLabel = "Server ����";
		List<String> serverComboBoxItems = propService.getMonitoringServerNameList();

		initAndAddMonitoringAnchorPane(archiveUsageMAP, archiveUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(tableSpaceUsageMAP, tableSpaceUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(asmDiskUsageMAP, asmDiskUsageTabAP, dbComboBoxLabel, dbComboBoxItems);
		initAndAddMonitoringAnchorPane(osDiskUsageMAP, osDiskUsageTabAP, serverComboBoxLabel, serverComboBoxItems);
	}

	/**
	 * ����͸� AnchorPane �߰��ϰ� ��Ҹ� �ʱ�ȭ�Ѵ�.
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
		monitoringAP.setAliasComboBoxLabelText(labelText); // ComboBox ���� Lebel Text ����
		monitoringAP.setAliasComboBoxItems(comboBoxItems); // ComboBox Items ����
		parentAP.getChildren().add(monitoringAP); // Monitoring AnchorPane�� �θ� Node�� �߰�
	}

	/**
	 * [����] - ����͸��� �����Ѵ�.
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
}
