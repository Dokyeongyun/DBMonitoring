package Root.Utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneUtils {

	public static void movePage(Parent parent, Stage primaryStage, double width, double height) {
        primaryStage.setScene(new Scene(parent, width, height));
	}
}
