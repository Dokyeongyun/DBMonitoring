package root.javafx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

	public static void showAlert(AlertType alertType, String alertHeaderText, String alertContentText) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(alertHeaderText);
		alert.setContentText(alertContentText);
		alert.getDialogPane().getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/javaFx.css");
		alert.getDialogPane().getStyleClass().add("basic-font");
		alert.show();
	}

	public static void showPropertyNotLoadedAlert() {
		showAlert(AlertType.ERROR, "설정파일 Load", "설정파일이 Load되지 않았습니다. 설정파일을 확인해주세요.");
	}
}
