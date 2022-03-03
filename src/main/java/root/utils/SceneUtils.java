package root.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import root.applications.Program;

public class SceneUtils {

	public static void movePage(Parent parent) {
		Stage primaryStage = Program.stage;
		movePage(parent, primaryStage, primaryStage.getWidth() - 15, primaryStage.getHeight() - 38);
	}

	public static void movePage(Parent parent, Stage primaryStage, double width, double height) {
		primaryStage.setScene(new Scene(parent, width, height));
		primaryStage.show();
	}
}
