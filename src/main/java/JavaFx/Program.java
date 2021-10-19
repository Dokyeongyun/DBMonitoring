package JavaFx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Program extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.setProperty("prism.lcdtext", "false"); // ��Ƽ�ٸ���� (Font �ε巴��)
		
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothic.ttf"), 10);
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothicBold.ttf"), 10);
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothicLight.ttf"), 10);
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothicUltraLight.ttf"), 10);

		FXMLLoader homeloader = new FXMLLoader();
		homeloader.setLocation(getClass().getResource("resources/fxml/Home.fxml"));
		AnchorPane homePane = homeloader.load();
		
		Scene scene = new Scene(homePane);
		scene.getStylesheets().add(getClass().getResource("resources/css/javaFx.css").toExternalForm());
		
		primaryStage.setTitle("DB Monitoring Window Program");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}