package root.javafx.CustomView;

import java.io.IOException;

import com.jfoenix.controls.JFXComboBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import root.core.domain.AlertLogCommand;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;

public class ServerConnectionInfoAnchorPane extends AnchorPane {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@FXML
	TextField serverNameTF;

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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ServerConnectionInfoAnchorPane.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// "※프로퍼티파일을 열거나 접속정보를 추가해주세요."
	public void setInitialValue(JschConnectionInfo jsch) {
		alertLogDateFormatCB.getItems()
				.addAll(propertyRepository.getCommonResources("server.setting.dateformat.combo"));

		serverNameTF.setText(jsch.getServerName());
		hostTF.setText(jsch.getHost());
		portTF.setText(jsch.getPort());
		userTF.setText(jsch.getUserName());
		passwordPF.setText(jsch.getPassword());
		alertLogFilePathTF.setText(jsch.getAlc().getReadFilePath());
		alertLogDateFormatCB.getSelectionModel().select(jsch.getAlc().getDateFormat());
	}

	public JschConnectionInfo getInputValues() {
		JschConnectionInfo jsch = new JschConnectionInfo();
		jsch.setServerName(this.serverNameTF.getText());
		jsch.setHost(this.hostTF.getText());
		jsch.setPort(this.portTF.getText());
		jsch.setUserName(this.userTF.getText());
		jsch.setPassword(this.passwordPF.getText());
		AlertLogCommand alc = new AlertLogCommand();
		alc.setReadFilePath(this.alertLogFilePathTF.getText());
		alc.setDateFormat(this.alertLogDateFormatCB.getSelectionModel().getSelectedItem());
		jsch.setAlc(alc);
		return jsch;
	}
}
