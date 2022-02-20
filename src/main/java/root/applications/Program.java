package root.applications;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;

public class Program extends Application {

	PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();

	@Override
	public void start(Stage primaryStage) throws Exception {

		// configuration load
		propRepo.loadCombinedConfiguration();

		// fxml load
		System.setProperty("prism.lcdtext", "false"); // ��Ƽ�ٸ���� (Font �ε巴��)

		String[] fontFiles = new File("./src/main/resources/font").list();
		for(String font : fontFiles) {
			Font.loadFont(getClass().getResourceAsStream("./src/main/resources/font/" + font), 10);
		}

		FXMLLoader homeloader = new FXMLLoader();
		homeloader.setLocation(getClass().getResource("/fxml/Home.fxml"));
		AnchorPane homePane = homeloader.load();

		Scene scene = new Scene(homePane, 1200, 650);
		scene.getStylesheets().add(getClass().getResource("/css/javaFx.css").toExternalForm());

		primaryStage.setTitle("DB Monitoring Window Program");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
