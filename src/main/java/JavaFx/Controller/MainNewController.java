package JavaFx.Controller;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import Root.Application.Application;
import Root.Utils.PropertiesUtils;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainNewController implements Initializable {
	
	@FXML SplitPane rootSplitPane;
	
	// Left SplitPane Region
	@FXML Button homeBtn;
	@FXML Button settingMenuBtn;
	@FXML Button runMenuBtn;
	
	// Right SplitPane Region
	@FXML StackPane rightStackPane;
	
	// Setting Menu Region
	@FXML ScrollPane settingScrollPane;
	@FXML AnchorPane settingMainContentAnchorPane;
	@FXML VBox monitoringElementsVBox;
	@FXML JFXButton settingSaveBtn;
	@FXML Button fileChooserBtn;				// .properties 파일을 선택하기 위한 FileChooser
	@FXML TextField fileChooserText;			// .properties 파일 경로를 입력/출력하는 TextField
	@FXML AnchorPane connectInfoAnchorPane;		
	@FXML FlowPane connectInfoFlowPane;			// DB접속정보 VBOX, Server접속정보 VOX를 담는 컨테이너
	@FXML StackPane dbConnInfoStackPane;		// DB접속정보 설정 그리드를 담는 컨테이너
	
	// Run Menu Region
	@FXML Button monitoringRunBtn;

	// TODO 추후, Properties 파일에서 읽어오기
	String[] dbMonitorings = new String[] {"Archive Usage", "TableSpace Usage", "ASM Disk Usage"};
	String[] serverMonitorings = new String[] {"OS Disk Usage", "Alert Log"};
	
	String[] dbNames = PropertiesUtils.propConfig.getString("dbnames").split("/");
	String[] serverNames = PropertiesUtils.propConfig.getString("servernames").split("/");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// [설정] - [모니터링 여부 설정] TAB 동적 요소 생성
		createMonitoringElements(monitoringElementsVBox, dbMonitorings, dbNames);
		createMonitoringElements(monitoringElementsVBox, serverMonitorings, serverNames);
		
		// [설정] - [접속정보 설정] TAB 동적 요소 생성
		createConnInfoElements(dbConnInfoStackPane);
	}
	
	/**
	 * [설정] - [접속정보 설정] - .properties 파일을 선택하기 위한 FileChooser를 연다.
	 * @param e
	 */
	public void openFileChooser(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog((Stage) rootSplitPane.getScene().getWindow());
		if(selectedFile != null) {
			fileChooserText.setText(selectedFile.getAbsolutePath());	
		}
	}
	
	/**
	 * [실행] - 모니터링을 시작한다.
	 * @param e
	 */
	public void runMonitoring(ActionEvent e) {
		Application.main(new String[] {});
	}
	
	/**
	 * 우측 StackPane의 top layer를 변경한다.
	 * @param containerPaneName
	 */
	public void changeStackPaneFrontMenu(ActionEvent e) {
		String thisMenuBtnId = ((Button)e.getSource()).getId();
		String thisBorderPaneId = thisMenuBtnId.substring(0, thisMenuBtnId.indexOf("Btn")) + "BorderPane";
		
		ObservableList<Node> childs = rightStackPane.getChildren();
		for(Node n : childs) {
			if(n.getId().equals(thisBorderPaneId)) {
				n.toFront();
				break;
			}
		}
	}
	
	/**
	 * [설정] - [접속정보 설정] - 변경사항을 .properties파일에 저장한다.
	 * @param e
	 */
	public void saveConnInfoSettings(ActionEvent e) {
		
	}

	/**
	 * [설정] - [모니터링 여부 설정] - 사용자가 선택한 설정에 따라 설정파일(.properties)을 생성 또는 수정한다.
	 * @param e
	 */
	public void saveSettings(ActionEvent e) {
		
		// TODO 설정파일을 저장할 경로 및 파일몀을 지정할 수 있도록 UI 생성하기
		
		try {
			File settingFile = new File(PropertiesUtils.configurationPath);
			if(settingFile.exists() == false) {
				settingFile.createNewFile();
			}
	
			for(Node n : monitoringElementsVBox.lookupAll("JFXToggleButton")) {
				JFXToggleButton thisToggle = (JFXToggleButton) n;
				PropertiesUtils.propConfig.setProperty(thisToggle.getId(), thisToggle.isSelected());
			}
			PropertiesUtils.save();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
			String headerToggleId = mName.replaceAll("\\s", "") + "TotalToggleBtn";
			
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
			headerToggleBtn.setId(headerToggleId);
			headerToggleBtn.setSize(6);
			headerToggleBtn.setAlignment(Pos.CENTER);
			headerToggleBtn.setSelected(PropertiesUtils.propConfig.containsKey(headerToggleId) == false ? true : PropertiesUtils.propConfig.getBoolean(headerToggleId));
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
				String contentToggleId = mName.replaceAll("\\s", "") + s + "ToggleBtn";
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
				contentToggleBtn.setId(contentToggleId);
				contentToggleBtn.setSize(4);
				contentToggleBtn.setMinWidth(40);
				contentToggleBtn.setMaxWidth(40);
				contentToggleBtn.setPrefHeight(40);
				contentToggleBtn.setAlignment(Pos.CENTER);
				contentToggleBtn.setSelected(PropertiesUtils.propConfig.containsKey(contentToggleId) == false ? true : PropertiesUtils.propConfig.getBoolean(contentToggleId));
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
							if(thisToggle.getId().equals(headerToggleId)) { // 부모 Toggle
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
							if(thisToggle.getId().equals(headerToggleId) == false) { // 자식 Toggle
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
								if(thisToggle.getId().equals(headerToggleId)) { // 부모 Toggle
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

	
	private void createConnInfoElements(StackPane rootStackPane) {
		
		AnchorPane dbConnInfoDetailAP = new AnchorPane();
		GridPane dbConnInfoDetailGP = new GridPane();
		
		ColumnConstraints col1 = new ColumnConstraints();
		ColumnConstraints col2 = new ColumnConstraints();
		ColumnConstraints col3 = new ColumnConstraints();
		ColumnConstraints col4 = new ColumnConstraints();
		col1.setPercentWidth(15);
		col2.setPercentWidth(50);
		col3.setPercentWidth(15);
		col4.setPercentWidth(20);
		
		dbConnInfoDetailGP.getColumnConstraints().addAll(col1, col2, col3, col4);
	}
}
