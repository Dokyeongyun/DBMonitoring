package JavaFx;

import Root.Utils.PropertiesUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Program extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// configuration load
		loadConfiguration();
		
		// fxml load
		System.setProperty("prism.lcdtext", "false"); // 救萍举府绢教 (Font 何靛反霸)
		
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
	
	public void loadConfiguration () {
		//String propertyFilePathName = "C:\\Users\\aserv\\Documents\\WorkSpace_DBMonitoring_Quartz\\DBMonitoring\\config\\application.properties";
		String propertiesFilePath = "C:\\Users\\aserv\\Documents\\Workspace\\DBMonitoring\\DBMonitoring\\config\\application.properties";
		try {
			PropertiesUtils.loadAppConfiguration(propertiesFilePath);
		}catch(Exception e) {
			System.out.println("configuration loading error\n"+e+"\n");
			return;
		}
	}
}
