package root.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

	public static void showAlert(AlertType alertType, String alertHeaderText, String alertContentText) {
		Alert failAlert = new Alert(alertType);
		failAlert.setHeaderText(alertHeaderText);
		failAlert.setContentText(alertContentText);
		failAlert.getDialogPane().setStyle("-fx-font-family: NanumGothic;");
		failAlert.show();
	}
}
