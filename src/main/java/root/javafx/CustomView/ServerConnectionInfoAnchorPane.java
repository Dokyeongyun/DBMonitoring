package root.javafx.CustomView;

import java.io.IOException;

import com.jfoenix.controls.JFXComboBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.Data;
import lombok.EqualsAndHashCode;
import root.core.domain.JschConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;

@EqualsAndHashCode(callSuper = false)
@Data
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
		portTF.setText(String.valueOf(jsch.getPort()));
		userTF.setText(jsch.getUserName());
		passwordPF.setText(jsch.getPassword());
		alertLogFilePathTF.setText(jsch.getAlc().getReadFilePath());
		alertLogDateFormatCB.getSelectionModel().select(jsch.getAlc().getDateFormat());
	}
}
