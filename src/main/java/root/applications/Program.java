package root.applications;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import root.utils.PropertiesUtils;

public class Program extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// configuration load
		PropertiesUtils.loadCombinedConfiguration();
		
		// fxml load
		System.setProperty("prism.lcdtext", "false"); // 救萍举府绢教 (Font 何靛反霸)
		
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothic.ttf"), 10);
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothicBold.ttf"), 10);
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothicLight.ttf"), 10);
		Font.loadFont(getClass().getResourceAsStream("resources/font/NanumBarunGothicUltraLight.ttf"), 10);

		FXMLLoader homeloader = new FXMLLoader();
		homeloader.setLocation(getClass().getResource("resources/fxml/Home.fxml"));
		AnchorPane homePane = homeloader.load();
		
		Scene scene = new Scene(homePane, 1200, 650);
		scene.getStylesheets().add(getClass().getResource("resources/css/javaFx.css").toExternalForm());
		
		primaryStage.setTitle("DB Monitoring Window Program");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
