package root.javafx.CustomView.dialogUI;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CustomTextInputDialog extends TextInputDialog {

	public CustomTextInputDialog(String title, String headerText, String contentText) {
		// ICON
		setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PENCIL, "30"));

		// CSS
		getDialogPane().getStylesheets().add(System.getProperty("resourceBaseDir") + "/css/dialog.css");
		getDialogPane().getStyleClass().add("textInputDialog");

		// Dialog ICON
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		Image image = new Image(
				getClass().getResource(System.getProperty("resourceBaseDir") + "/image/add_icon.png").toString());
		stage.getIcons().add(image);
		
		// Button Custom
		ButtonType okButton = new ButtonType("ют╥б", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().removeAll(ButtonType.OK, ButtonType.CANCEL);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
		
		// Content
		setTitle(title);
		setHeaderText(headerText);
		setContentText(contentText);
	}
}
