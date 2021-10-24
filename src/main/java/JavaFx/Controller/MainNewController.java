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

	// TODO ����, Properties ���Ͽ��� �о����
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
	 * ���� ��� Home Icon(fxId: homeBtn) onAction Event
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
	 * ����͸� ���� ������ ��ҵ� ���� ����
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
						1. ������Ұ� ���õǾ��� ��, 
							1.1. �θ��Ұ� ���õǾ����� Ȯ��
						 		1.1.1. ���õ� 	- break;
						 		1.1.2. ���þȵ� 	- isSelected = true
						2. ������Ұ� ���õ��� �ʾ��� ��,
							2.1. �θ��Ұ� ���õǾ����� Ȯ��
								2.1.1. ���þȵ�	- break;
								2.1.2. ���õ�		
									2.1.2.1. ��� ������� ���ÿ��� Ȯ��
										2.1.2.1.1. ��� ������� ���õ������� - �θ��� isSelected = false;
					*/
					if(isSelected == true) {
						for(Node n : eachWrapVBox.lookupAll("JFXToggleButton")) {
							JFXToggleButton thisToggle = (JFXToggleButton) n;
							if(thisToggle.getId().equals(mName+" ToggleBtn")) { // �θ� Toggle
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
							if(thisToggle.getId().equals(mName+" ToggleBtn") == false) { // �ڽ� Toggle
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
								if(thisToggle.getId().equals(mName+" ToggleBtn")) { // �θ� Toggle
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
