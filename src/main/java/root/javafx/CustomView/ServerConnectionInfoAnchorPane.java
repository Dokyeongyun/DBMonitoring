package root.javafx.CustomView;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXComboBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import root.core.domain.AlertLogCommand;
import root.core.domain.JschConnectionInfo;
import root.core.domain.enums.ServerOS;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.DI.DependencyInjection;

@EqualsAndHashCode(callSuper = false)
@Data
public class ServerConnectionInfoAnchorPane extends ConnectionInfoAP {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@FXML
	TextField serverNameTF;

	@FXML
	JFXComboBox<ServerOS> serverOSCB;

	@FXML
	TextField hostTF;

	@FXML
	TextField portTF;

	@FXML
	TextField userTF;

	@FXML
	PasswordField passwordPF;

	@FXML
	TextField alertLogFilePathTF;

	@FXML
	JFXComboBox<String> alertLogDateFormatCB;

	public ServerConnectionInfoAnchorPane() {
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/ServerConnectionInfoAnchorPane.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	boolean isAnyEmptyInputForConnectionTest() {
		return StringUtils.isAnyEmpty(hostTF.getText(), portTF.getText(), userTF.getText(), passwordPF.getText());
	}

	public void init() {
		// Set textFormatter
		portTF.setTextFormatter(new NumberTextFormatter());

		// Set AlertLogDateFormat ComboBox values
		alertLogDateFormatCB.getItems()
				.addAll(propertyRepository.getCommonResources("server.setting.dateformat.combo"));
	}

	public void setInitialValue(JschConnectionInfo jsch) {
		serverNameTF.setText(jsch.getServerName());
		serverOSCB.getSelectionModel().select(jsch.getServerOS());
		hostTF.setText(jsch.getHost());
		portTF.setText(String.valueOf(jsch.getPort()));
		userTF.setText(jsch.getUserName());
		passwordPF.setText(jsch.getPassword());
		alertLogFilePathTF.setText(jsch.getAlc().getReadFilePath());
		alertLogDateFormatCB.getSelectionModel().select(jsch.getAlc().getDateFormat());
	}

	public JschConnectionInfo getInputValues() {
		JschConnectionInfo jsch = new JschConnectionInfo();
		jsch.setServerName(serverNameTF.getText());
		jsch.setServerOS(serverOSCB.getValue());
		jsch.setHost(hostTF.getText());
		jsch.setPort(portTF.getText());
		jsch.setUserName(userTF.getText());
		jsch.setPassword(passwordPF.getText());
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadFilePath(alertLogFilePathTF.getText());
		alc.setDateFormat(alertLogDateFormatCB.getSelectionModel().getSelectedItem());
		jsch.setAlc(alc);
		return jsch;
	}

	public boolean isAnyEmptyInput() {
		return StringUtils.isAnyEmpty(serverOSCB.getSelectionModel().getSelectedItem().name(), hostTF.getText(),
				portTF.getText(), userTF.getText(), serverNameTF.getText(), passwordPF.getText(),
				alertLogFilePathTF.getText(), alertLogDateFormatCB.getSelectionModel().getSelectedItem());
	}
}
