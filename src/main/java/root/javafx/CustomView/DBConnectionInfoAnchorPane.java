package root.javafx.CustomView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.Event;
import javafx.event.EventHandler;
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

		// DB Url Generate Event Setting
		String dbms = "oracle";
		DBUrlGenerateEvent urlEvent = new DBUrlGenerateEvent(dbms);

		driverCB.setOnInputMethodTextChanged(urlEvent);
		hostTF.setOnKeyReleased(urlEvent);
		portTF.setOnKeyReleased(urlEvent);
		sidTF.setOnKeyReleased(urlEvent);

		urlTF.setOnKeyReleased(s -> {
			String text = ((TextField) s.getSource()).getText();
			Pattern p = Pattern
					.compile("(.*):(.*):" + driverCB.getSelectionModel().getSelectedItem() + ":@(.*):(.*)/(.*)");
			Matcher m = p.matcher(text);
			if (m.matches()) {
				hostTF.setText(m.group(3));
				portTF.setText(m.group(4));
				sidTF.setText(m.group(5));
			} else {
				urlEvent.handle(s);
			}
		});
	}
	
	public JdbcConnectionInfo getInputValues() {
		JdbcConnectionInfo jdbc = new JdbcConnectionInfo();
		jdbc.setJdbcDBName(this.aliasTF.getText());
		jdbc.setJdbcHost(this.hostTF.getText());
		jdbc.setJdbcPort(this.portTF.getText());
		jdbc.setJdbcDriver(this.driverCB.getSelectionModel().getSelectedItem());
		jdbc.setJdbcId(this.userTF.getText());
		jdbc.setJdbcPw(this.passwordPF.getText());
		jdbc.setJdbcSID(this.sidTF.getText());
		jdbc.setJdbcUrl(this.urlTF.getText());
		jdbc.setJdbcValidation("SELECT 1 FROM DUAL");
		jdbc.setJdbcConnections(10);
		return jdbc;
	}

	/**
	 * Ű �Է� �Ǵ� �޺��ڽ� ������ ���� �Էµ� Database ���������� �̿��� URL�� �����ϴ� �̺�Ʈ
	 * 
	 * @author DKY
	 *
	 */
	private class DBUrlGenerateEvent implements EventHandler<Event> {

		private String dbms;

		public DBUrlGenerateEvent(String dbms) {
			this.dbms = dbms;
		}

		@Override
		public void handle(Event event) {
			StringBuffer url = new StringBuffer();
			url.append("jdbc:").append(dbms).append(":").append(driverCB.getSelectionModel().getSelectedItem())
					.append(":@").append(hostTF.getText()).append(":").append(portTF.getText()).append("/")
					.append(sidTF.getText());

			urlTF.setText(url.toString());
		}
	}
}