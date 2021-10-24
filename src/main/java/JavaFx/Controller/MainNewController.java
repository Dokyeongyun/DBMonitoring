package JavaFx.Controller;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXToggleButton;

import Root.Utils.PropertiesUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;

public class MainNewController implements Initializable {
	
	@FXML Button homeBtn;
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

	/**
	 * 좌측 상단 Home Icon(fxId: homeBtn) onAction Event
	 * @param e
	 * @throws IOException
	 */
	public void goHomeStage(ActionEvent e) throws IOException {
		Scene originalScene = homeBtn.getScene();
		Parent home = FXMLLoader.load(getClass().getResource("../resources/fxml/Home.fxml"));
        Scene homeScene = new Scene(home, originalScene.getWidth(), originalScene.getHeight());
        Stage primaryStage = (Stage) homeBtn.getScene().getWindow();
        primaryStage.setScene(homeScene);
	}
	
	/**
	 * 모니터링 여부 설정할 요소들 동적 생성
	 * @param rootVBox
	 * @param monitoringElements
	 * @param elementContents
	 */
	private void createMonitoringElements(VBox rootVBox, String[] monitoringElements, String[] elementContents) {


		for(String mName : monitoringElements) {
			// Header
			VBox eachWrapVBox = new VBox();
			eachWrapVBox.setFillWidth(true);
			
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
			headerToggleBtn.setId(mName+" ToggleBtn");
			headerToggleBtn.setSize(6);
			headerToggleBtn.setAlignment(Pos.CENTER);
			headerToggleBtn.setSelected(true);
			headerToggleBtn.setOnAction((ActionEvent e) -> {
				boolean isSelected = ((JFXToggleButton) e.getSource()).isSelected();
				for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
					((JFXToggleButton) n).setSelected(isSelected);
				}
			});
			
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
				contentToggleBtn.setId(mName+" "+s+" ToggleBtn");
				contentToggleBtn.setSize(4);
				contentToggleBtn.setMinWidth(40);
				contentToggleBtn.setMaxWidth(40);
				contentToggleBtn.setPrefHeight(40);
				contentToggleBtn.setAlignment(Pos.CENTER);
				contentToggleBtn.setSelected(true);
				contentToggleBtn.setOnAction((ActionEvent e) -> {
					boolean isSelected = ((JFXToggleButton) e.getSource()).isSelected();
					
					/*
						1. 하위요소가 선택되었을 때, 
							1.1. 부모요소가 선택되었는지 확인
						 		1.1.1. 선택됨 	- break;
						 		1.1.2. 선택안됨 	- isSelected = true
						2. 하위요소가 선택되지 않았을 때,
							2.1. 부모요소가 선택되었는지 확인
								2.1.1. 선택안됨	- break;
								2.1.2. 선택됨		
									2.1.2.1. 모든 하위요소 선택여부 확인
										2.1.2.1.1. 모든 하위요소 선택되지않음 - 부모요소 isSelected = false;
					*/
					if(isSelected == true) {
						for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if(thisToggle.getId().equals(mName+" ToggleBtn")) { // 부모 Toggle
								if(thisToggle.isSelected() == false) {
									thisToggle.setSelected(true);
									break;
								}
							} 
						}
					} else {
						boolean isNotAllSelected = false;
						boolean isParentSelected = true;
						for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if(thisToggle.getId().equals(mName+" ToggleBtn") == false) { // 자식 Toggle
								if(thisToggle.isSelected() == true) {
									isNotAllSelected = true;
									break;
								}
							} else {
								if(thisToggle.isSelected() == false) {
									isParentSelected = false;
									break;
								}
							}
						}
						
						if(isNotAllSelected == false && isParentSelected == true) {
							for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
								JFXToggleButton thisToggle = (JFXToggleButton) n;
								if(thisToggle.getId().equals(mName+" ToggleBtn")) { // 부모 Toggle
									thisToggle.setSelected(false);
									break;
								} 
							}
						}
					}
				});
				
				contentHBox.getChildren().addAll(contentLabel, contentToggleBtn);
				contentFlowPane.getChildren().addAll(contentHBox);
			}
			
			eachWrapVBox.getChildren().addAll(headerHBox, contentFlowPane);

			rootVBox.getChildren().addAll(eachWrapVBox);
		}
		
		rootVBox.getChildren().add(new Separator());
	}
}
