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

	/**
	 * 키 입력 또는 콤보박스 선택을 통해 입력된 Database 접속정보를 이용해 URL을 생성하는 이벤트
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
