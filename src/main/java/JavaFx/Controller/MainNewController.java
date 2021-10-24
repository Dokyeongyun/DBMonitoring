package JavaFx.Controller;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import Root.Utils.PropertiesUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class MainNewController implements Initializable {

	@FXML SplitPane rootSplitPane;
	@FXML ScrollPane settingScrollPane;
	@FXML AnchorPane settingMainContentAnchorPane;
	@FXML VBox monitoringElementsVBox;

	// TODO 추후, Properties 파일에서 읽어오기
	String[] dbMonitorings = new String[] {"Archive Usage", "TableSpace Usage", "ASM Disk Usage"};
	String[] serverMonitorings = new String[] {"OS Disk Usage", "Alert Log"};
	
	String[] dbNames = PropertiesUtils.propConfig.getString("dbnames").split("/");
	String[] serverNames = PropertiesUtils.propConfig.getString("servernames").split("/");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		createMonitoringElements(monitoringElementsVBox, dbMonitorings, dbNames);
		createMonitoringElements(monitoringElementsVBox, serverMonitorings, serverNames);
	}

	private void createMonitoringElements(VBox rootVBox, String[] monitoringElements, String[] elementContents) {

		for(String mName : monitoringElements) {
			// Header
			HBox headerHBox = new HBox();
			headerHBox.setFillHeight(true);
			headerHBox.setPrefHeight(40);
			
			ImageView headerImage = new ImageView();
			try {
				File imageFile = new File("../DBMonitoring/src/main/java/JavaFx/resources/image/orange_point_icon.png");
				headerImage.setImage(new Image(new FileInputStream(imageFile)));
				headerImage.setPreserveRatio(true);
				headerImage.setFitHeight(10);
				headerImage.prefHeight(40);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			Label headerLabel = new Label();
			headerLabel.setText(mName);
			headerLabel.setTextAlignment(TextAlignment.LEFT);
			headerLabel.setAlignment(Pos.CENTER_LEFT);
			headerLabel.setPrefWidth(200);
			headerLabel.setPrefHeight(40);
			headerLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: BLACK; -fx-font-weight: bold; -fx-font-size: 16px;");
			headerLabel.setGraphic(headerImage);
			headerLabel.setGraphicTextGap(10);
			
			JFXToggleButton headerToggleBtn = new JFXToggleButton();
			headerToggleBtn.setSize(6);
			headerToggleBtn.setAlignment(Pos.CENTER);
			headerToggleBtn.setSelected(true);
			
			headerHBox.getChildren().addAll(headerLabel, headerToggleBtn);
			
			// Content
			FlowPane contentFlowPane = new FlowPane();
			contentFlowPane.prefWidthProperty().bind(rootVBox.widthProperty());
			contentFlowPane.minWidthProperty().bind(rootVBox.minWidthProperty());
			
			for(String s : elementContents) {
				HBox contentHBox = new HBox();
				Label contentLabel = new Label();
				contentLabel.setText(s);
				contentLabel.setTextAlignment(TextAlignment.LEFT);
				contentLabel.setAlignment(Pos.CENTER_LEFT);
				contentLabel.setMinWidth(80);
				contentLabel.setMaxWidth(80);
				contentLabel.setPrefHeight(40);
				contentLabel.setStyle("-fx-font-family: NanumGothic; -fx-text-fill: BLACK; -fx-font-weight: bold; -fx-font-size: 12px;");
				
				JFXToggleButton contentToggleBtn = new JFXToggleButton();
				contentToggleBtn.setSize(4);
				contentToggleBtn.setMinWidth(40);
				contentToggleBtn.setMaxWidth(40);
				contentToggleBtn.setPrefHeight(40);
				contentToggleBtn.setAlignment(Pos.CENTER);
				contentToggleBtn.setSelected(true);
				
				contentHBox.getChildren().addAll(contentLabel, contentToggleBtn);
				contentFlowPane.getChildren().addAll(contentHBox);
			}
			
			rootVBox.getChildren().addAll(headerHBox, contentFlowPane);
		}
//		Separator hSeparator = ;
		rootVBox.getChildren().add(new Separator());
	}
}
