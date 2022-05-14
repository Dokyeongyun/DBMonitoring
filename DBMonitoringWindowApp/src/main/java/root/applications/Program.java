package root.applications;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import root.core.repository.constracts.PropertyRepository;
import root.core.repository.implement.PropertyRepositoryImpl;
import root.javafx.Controller.HomeController;
import root.javafx.Controller.LeftMenuController;
import root.javafx.DI.DependencyInjection;

@Slf4j
public class Program extends Application {

	public static Stage stage;

	PropertyRepository propRepo = PropertyRepositoryImpl.getInstance();

	@Override
	public void start(Stage primaryStage) throws Exception {

		stage = primaryStage;

		setUpDependecyInjector();

		// configuration load
		propRepo.loadCombinedConfiguration();

		// fxml load
		System.setProperty("prism.lcdtext", "false"); // 안티앨리어싱 (Font 부드럽게)

		Parent root = DependencyInjection.load("/fxml/Home.fxml");
		primaryStage.setTitle("DB Monitoring Window Program");
		primaryStage.setScene(new Scene(root, 1200, 650));
		primaryStage.show();
	}

	public static void main(String[] args) {
		if (System.getProperty("resourceBaseDir") == null) {
			System.setProperty("resourceBaseDir", "");
		}

		log.info("Start GUI Program");
		launch(args);
	}

	private void setUpDependecyInjector() {
		// save the factory in the injector
		DependencyInjection.addInjectionMethod(HomeController.class, param -> {
			return new HomeController();
		});
		DependencyInjection.addInjectionMethod(LeftMenuController.class, param -> {
			return new LeftMenuController();
		});
	}
}
