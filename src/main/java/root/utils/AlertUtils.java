package root.utils;

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
}
