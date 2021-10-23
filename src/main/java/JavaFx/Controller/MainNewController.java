package JavaFx.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class MainNewController implements Initializable {

	@FXML SplitPane rootSplitPane;
	@FXML AnchorPane settingMainContentAnchorPane;
	@FXML VBox monitoringElementsVBox;
	String[] monitoringElements = new String[] {"Archive Usage", "TableSpace Usage", "ASM Disk Usage", "OS Disk Usage", "Alert Log"}; 
			
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		for(String mName : monitoringElements) {
			HBox titleHBox = new HBox();
			titleHBox.setFillHeight(true);
			titleHBox.setPrefHeight(40);
			
			ImageView titleImage = new ImageView();
			try {
				File imageFile = new File("../DBMonitoring/src/main/java/JavaFx/resources/image/orange_point_icon.png");
				titleImage.setImage(new Image(new FileInputStream(imageFile)));
				titleImage.setPreserveRatio(true);
				titleImage.setFitHeight(10);
				titleImage.prefHeight(40);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			Label titleLabel = new Label();
			titleLabel.setText(mName);
			titleLabel.setTextAlignment(TextAlignment.LEFT);
			titleLabel.setAlignment(Pos.CENTER_LEFT);
			titleLabel.setPrefWidth(200);
			titleLabel.setPrefHeight(40);
			titleLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: BLACK; -fx-font-weight: bold; -fx-font-size: 16px;");
			titleLabel.setGraphic(titleImage);
			titleLabel.setGraphicTextGap(10);
			
			JFXToggleButton titleToggleBtn = new JFXToggleButton();
			titleToggleBtn.setSize(6);
			titleToggleBtn.setAlignment(Pos.CENTER);
			titleToggleBtn.setSelected(true);
			
			titleHBox.getChildren().addAll(titleLabel, titleToggleBtn);
			
			monitoringElementsVBox.getChildren().add(titleHBox);
		}
	}

}
