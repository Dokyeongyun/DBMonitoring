package root.javafx.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.javafx.CustomView.CustomTreeTableView;
import root.javafx.CustomView.CustomTreeView;
import root.javafx.CustomView.MonitoringTableView;
import root.javafx.Model.MonitoringYN;

public class RunMenuController implements Initializable {

	@FXML
	AnchorPane connInfoSettingAP;

	@FXML
	AnchorPane presetSettingAP;

	@FXML
	ScrollPane mainScrollPane;

	@FXML
	AnchorPane scrollAP;

	@FXML
	SplitPane upperSplitPane;

	@FXML
	SplitPane lowerSplitPane;

	@FXML
	AnchorPane archiveAP;

	@FXML
	AnchorPane tableSpaceAP;

	@FXML
	AnchorPane asmDiskAP;

	@FXML
	AnchorPane osDiskAP;
	
	@FXML
	Label step4Label;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// �������� ����Ʈ TreeView
		CustomTreeView connInfoCtv = new CustomTreeView("�������� ����Ʈ", FontAwesomeIcon.LIST, true);
		connInfoCtv.addTreeItem("DB", new ArrayList<>(Arrays.asList("DB1", "DB2", "DB3")), FontAwesomeIcon.DATABASE);
		connInfoCtv.addTreeItem("Server", new ArrayList<>(Arrays.asList("Server1", "Server2", "Server3", "Server4")),
				FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(connInfoCtv, 80, 0, 0, 0);
		connInfoSettingAP.getChildren().add(connInfoCtv);

		// ����͸� ���� ����Ʈ TreeTableView
		List<MonitoringYN> list1 = new ArrayList<>();
		list1.add(new MonitoringYN("DB1", "N", "N", "N", "N"));
		list1.add(new MonitoringYN("DB2", "N", "N", "N", "N"));
		list1.add(new MonitoringYN("DB3", "N", "N", "N", "N"));
		List<MonitoringYN> list2 = new ArrayList<>();
		list2.add(new MonitoringYN("Server1", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server2", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server3", "Y", "Y", "Y", "Y"));
		list2.add(new MonitoringYN("Server4", "Y", "Y", "Y", "Y"));
		CustomTreeTableView presetCtv = new CustomTreeTableView("����͸� ���� ����Ʈ", FontAwesomeIcon.LIST);
		presetCtv.addTreeTableItem("DB", list1, FontAwesomeIcon.DATABASE);
		presetCtv.addTreeTableItem("Server", list2, FontAwesomeIcon.SERVER);
		setAnchorPaneAnchor(presetCtv, 80, 0, 0, 0);
		presetSettingAP.getChildren().add(presetCtv);

		// ������ TableView ���� �� Column �߰�
		MonitoringTableView<ArchiveUsage> archiveTable = addMonitoringTableView(archiveAP, ArchiveUsage.class);
		archiveTable.addColumn("Archive", "archiveName");
		archiveTable.addColumn("��뷮(%)", "usedPercent");
		
		MonitoringTableView<TableSpaceUsage> tableSpaceTable = addMonitoringTableView(tableSpaceAP,
				TableSpaceUsage.class);
		tableSpaceTable.addColumn("���̺����̽�", "tableSpaceName");
		tableSpaceTable.addColumn("��뷮(%)", "usedPercent");
		
		MonitoringTableView<ASMDiskUsage> asmDiskTable = addMonitoringTableView(asmDiskAP, ASMDiskUsage.class);
		asmDiskTable.addColumn("��ũ �׷�", "asmDiskGroupName");
		asmDiskTable.addColumn("��ũ Ÿ��", "asmDiskGroupType");	
		asmDiskTable.addColumn("��뷮(%)", "usedPercent");
		
		MonitoringTableView<OSDiskUsage> osDiskTable = addMonitoringTableView(osDiskAP, OSDiskUsage.class);
		osDiskTable.addColumn("���� �ý���", "fileSystem");
		osDiskTable.addColumn("����Ʈ ��ġ", "mountedOn");
		osDiskTable.addColumn("��뷮(%)", "usedPercent");
	}

	/**
	 * ����޴� ScrollPane scroll event
	 * 
	 * @param e
	 */
	public void scroll(ScrollEvent e) {
		if (e.getDeltaX() == 0 && e.getDeltaY() != 0) {
			// TODO ��ũ�Ѽӵ� ������ �� �ֵ��� ����
			double deltaY = e.getDeltaY() * 3;
			double width = mainScrollPane.getWidth();
			double vvalue = mainScrollPane.getHvalue();
			mainScrollPane.setHvalue(vvalue - deltaY / width);
		}
	}

	/**
	 * AnchorPane�� Anchor�� �� �ٷ� �����ϱ� ���� �޼���
	 * 
	 * @param node   AnchorPane�� �ڽ� ���
	 * @param top    top anchor
	 * @param right  right anchor
	 * @param bottom bottom anchor
	 * @param left   left anchor
	 */
	private void setAnchorPaneAnchor(Node node, double top, double right, double bottom, double left) {
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
		AnchorPane.setLeftAnchor(node, left);
	}

	/**
	 * ����͸� ���� ��� TableView�� �θ� AnchorPane�� �߰��Ѵ�.
	 * 
	 * @param <T>            ����͸� Ÿ��
	 * @param parent         �θ� AnchorPane
	 * @param monitoringType ����͸� Ÿ�� Ŭ����
	 * @return ������ TableView
	 */
	private <T> MonitoringTableView<T> addMonitoringTableView(AnchorPane parent,
			Class<? extends MonitoringResult> monitoringType) {
		MonitoringTableView<T> tableView = new MonitoringTableView<>(monitoringType);
		setAnchorPaneAnchor(tableView, 20, 0, 0, 0);
		parent.getChildren().add(tableView);
		return tableView;
	}
}
