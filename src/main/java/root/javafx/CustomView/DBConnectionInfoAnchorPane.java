package root.javafx.CustomView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXComboBox;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import root.core.domain.JdbcConnectionInfo;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;

public class DBConnectionInfoAnchorPane extends ConnectionInfoAP {

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

	public void init() {
		// DB Url Generate Event Setting
		String dbms = "oracle";
		DBConnInfoOnChangedEvent changedEvent = new DBConnInfoOnChangedEvent(dbms);
		DBConnInfoOnChangedActionEvent changedActionEvent = new DBConnInfoOnChangedActionEvent(dbms);
		
		// Set event
		driverCB.setOnAction(changedActionEvent);
		hostTF.setOnKeyReleased(changedEvent);
		portTF.setOnKeyReleased(changedEvent);
		sidTF.setOnKeyReleased(changedEvent);
		userTF.setOnKeyReleased(changedEvent);
		passwordPF.setOnKeyReleased(changedEvent);
		aliasTF.setOnKeyReleased(changedEvent);
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
				changedEvent.handle(s);
			}
		});
		
		// Set driver ComboBox values
		driverCB.getItems().addAll(propertyRepository.getOracleDrivers());
	}

	public void setInitialValue(JdbcConnectionInfo jdbc) {
		hostTF.setText(jdbc.getJdbcHost());
		sidTF.setText(jdbc.getJdbcSID());
		userTF.setText(jdbc.getJdbcId());
		passwordPF.setText(jdbc.getJdbcPw());
		urlTF.setText(jdbc.getJdbcUrl());
		portTF.setText(jdbc.getJdbcPort());
		driverCB.getSelectionModel().select(jdbc.getJdbcDriver());
		aliasTF.setText(jdbc.getJdbcDBName());
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

	public boolean isAnyEmptyInput() {
		return StringUtils.isAnyEmpty(hostTF.getText(), portTF.getText(), sidTF.getText(), userTF.getText(),
				passwordPF.getText(), driverCB.getSelectionModel().getSelectedItem());
	}
	
	private String generateURL(String dbms) {
		StringBuffer url = new StringBuffer();
		url.append("jdbc:").append(dbms).append(":")
				.append(driverCB.getSelectionModel().getSelectedItem() == null ? "" : driverCB.getSelectionModel().getSelectedItem())
				.append(":@")
				.append(hostTF.getText() == null ? "" : hostTF.getText()).append(":")
				.append(portTF.getText() == null ? "" : portTF.getText()).append("/")
				.append(sidTF.getText() == null ? "" : sidTF.getText());
		return url.toString();
	}
	
	private void setDBConnTestBtnDisable(Node node) {
		try {
			// Find Top Node
			Node topParent = null;
			while (true) {
				if (node.getParent() == null) {
					break;
				}
				node = node.getParent();
				topParent = node;
			}

			if (topParent == null) {
				System.out.println("TopParent is Null");
				return;
			}

			// DB Connection test button lookup and setDisable
			topParent.lookup("#connTestBtn").setDisable(isAnyEmptyInput());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void dbConnInfoChangedEventHandle(String dbms, Event event) {
		// Generate URL
		urlTF.setText(generateURL(dbms));
		
		// Find Top Parent and set disable DBConnTestBtn
		setDBConnTestBtnDisable((Node) event.getTarget());
	}
	
	/**
	 * 키 입력을 통해 입력된 Database 접속정보를 이용해 URL을 생성하고,
	 * 입력된 값에 따라 DB 연동테스트 버튼을 활성화/비활성화한다.
	 * 
	 * @author DKY
	 *
	 */
	private class DBConnInfoOnChangedEvent implements EventHandler<Event> {

		private String dbms;

		public DBConnInfoOnChangedEvent(String dbms) {
			this.dbms = dbms;
		}
		
		@Override
		public void handle(Event event) {
			System.out.println("DBConnInfoOnKeyReleasedEvent Event Fire!");
			dbConnInfoChangedEventHandle(dbms, event);
		}
	}
	
	/**
	 * 콤보박스 선택을 통해 입력된 Database 접속정보를 이용해 URL을 생성하고,
	 * 입력된 값에 따라 DB 연동테스트 버튼을 활성화/비활성화한다.
	 * 
	 * @author DKY
	 *
	 */
	private class DBConnInfoOnChangedActionEvent implements EventHandler<ActionEvent> {

		private String dbms;

		public DBConnInfoOnChangedActionEvent(String dbms) {
			this.dbms = dbms;
		}
		
		@Override
		public void handle(ActionEvent event) {
			System.out.println("DBConnInfoOnKeyReleasedEvent Event Fire!");
			dbConnInfoChangedEventHandle(dbms, event);
		}
	}
}
