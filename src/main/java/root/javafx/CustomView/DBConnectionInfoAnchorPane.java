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
import root.core.domain.JdbcConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;

@EqualsAndHashCode(callSuper = false)
@Data
public class DBConnectionInfoAnchorPane extends AnchorPane {

	/* Dependency Injection */
	private PropertyRepository propertyRepository = PropertyRepositoryImpl.getInstance();

	@FXML
	TextField hostTF;

	@FXML
	TextField sidTF;

	@FXML
	TextField userTF;

	@FXML
	PasswordField passwordPF;

	@FXML
	TextField urlTF;

	@FXML
	TextField portTF;

	@FXML
	JFXComboBox<String> driverCB;

	@FXML
	TextField aliasTF;

	public DBConnectionInfoAnchorPane() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBConnectionInfoAnchorPane.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setInitialValue(JdbcConnectionInfo jdbc) {
		driverCB.getItems().addAll(propertyRepository.getOracleDrivers());

		hostTF.setText(jdbc.getJdbcHost());
		sidTF.setText(jdbc.getJdbcSID());
		userTF.setText(jdbc.getJdbcId());
		passwordPF.setText(jdbc.getJdbcPw());
		urlTF.setText(jdbc.getJdbcUrl());
		portTF.setText(jdbc.getJdbcPort());
		driverCB.getSelectionModel().select(jdbc.getJdbcOracleDriver());
		aliasTF.setText(jdbc.getJdbcDBName());
	}
}
