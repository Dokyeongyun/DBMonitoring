package root.javafx.Controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import root.core.domain.MonitoringResult;
import root.core.domain.enums.UsageUIType;
import root.javafx.CustomView.MonitoringTableViewContainer;
import root.javafx.DI.DependencyInjection;
import root.utils.DateUtils;

public class MonitoringTableViewPagingBox extends AnchorPane {

	@FXML
	private FontAwesomeIconView iconView;

	@FXML
	private Label aliasLabel;

	@FXML
	private Label monitoringTimeLabel;

	@FXML
	private StackPane tableViewSP;

	private Map<AliasInfo, MonitoringTableViewContainer> tableViewContainerMap = new LinkedHashMap<>();

	public MonitoringTableViewPagingBox(String monitoringType) {
		try {
			FXMLLoader loader = DependencyInjection.getLoader("/fxml/MonitoringTableViewPagingBox.fxml");
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		iconView.setIcon(monitoringType.equals("DB") ? FontAwesomeIcon.DATABASE : FontAwesomeIcon.SERVER);
		setMonitoringTimeLabel(new Date());
	}

	/**
	 * Alias���� ����͸� TableView�� ������ �����̳ʸ� �����Ѵ�.
	 * 
	 * @param alias
	 * @param type
	 */
	public void addMonitoringTableViewContainer(String alias, Class<? extends MonitoringResult> type) {
		if (tableViewContainerMap.get(findAliasInfoByAlias(alias)) == null) {
			MonitoringTableViewContainer container = new MonitoringTableViewContainer();
			tableViewContainerMap.put(new AliasInfo(alias, findLastAliasInfoIndex() + 1), container);
			tableViewSP.getChildren().add(container);
		}

		MonitoringTableViewContainer container = tableViewContainerMap.get(findAliasInfoByAlias(alias));
		container.addMonitoringTableView(type);
		container.setUsageUIType(type, UsageUIType.NUMERIC); // default
		tableViewContainerMap.put(findAliasInfoByAlias(alias), container);
	}

	/**
	 * ����͸� ��� TableView�� ��뷮 �÷��� UI Ÿ���� �����Ѵ�.
	 * 
	 * @param alias
	 * @param type
	 * @param uiType
	 */
	public void setMonitoringTableViewUsageUIType(String alias, Class<? extends MonitoringResult> type,
			UsageUIType uiType) {
		tableViewContainerMap.get(findAliasInfoByAlias(alias)).setUsageUIType(type, uiType);
	}

	/**
	 * ����͸� ��� TableView�� �����ͼ��� �����Ѵ�.
	 * 
	 * @param <T>
	 * @param alias
	 * @param type
	 * @param dataList
	 */
	public <T extends MonitoringResult> void setMonitoringTableViewData(String alias, Class<T> type, List<T> dataList) {
		tableViewContainerMap.get(findAliasInfoByAlias(alias)).setTableData(type, dataList);
		showTableViewContainer(alias);
	}

	/**
	 * Ư�� Alias�� TableViewContainer�� �� ������ �������� ���� �����Ѵ�.
	 * 
	 * @param alias
	 */
	private void showTableViewContainer(String alias) {
		setAliasLabel(alias);
		tableViewContainerMap.get(findAliasInfoByAlias(alias)).toFront();
	}

	/**
	 * Alias Label�� �ؽ�Ʈ�� �����Ѵ�.
	 * 
	 * @param alias
	 */
	private void setAliasLabel(String alias) {
		aliasLabel.setText(alias);
	}

	/**
	 * Monitoring Time Label�� �ؽ�Ʈ�� �����Ѵ�.
	 * 
	 * @param time
	 */
	public void setMonitoringTimeLabel(Date time) {
		monitoringTimeLabel.setText(DateUtils.format(time, "yyyy-MM-dd HH:mm:ss"));
	}

	private AliasInfo findAliasInfoByAlias(String alias) {
		return tableViewContainerMap.keySet()
				.stream()
				.filter(info -> info.getAlias().equals(alias))
				.findFirst()
				.orElse(null);
	}
	
	private AliasInfo findAliasInfoByIndex(int index) {
		return tableViewContainerMap.keySet()
				.stream()
				.filter(info -> info.getIndex() == index)
				.findFirst()
				.orElse(null);
	}

	private int findAliasInfoIndex(String alias) {
		return findAliasInfoByAlias(alias).getIndex();
	}
	
	private int findFirstAliasInfoIndex() {
		return tableViewContainerMap.keySet()
				.stream()
				.sorted()
				.map(info -> info.getIndex())
				.findFirst()
				.orElse(-1);
	}
	
	private int findLastAliasInfoIndex() {
		return tableViewContainerMap.keySet()
				.stream()
				.sorted(Collections.reverseOrder())
				.map(info -> info.getIndex())
				.findFirst()
				.orElse(-1);
	}
	
	private int findPrevAliasInfoIndex(int curIndex) {
		return tableViewContainerMap.keySet()
				.stream()
				.filter(info -> info.getIndex() < curIndex)
				.sorted(Collections.reverseOrder())
				.map(info -> info.getIndex())
				.findFirst()
				.orElse(-1);
	}
	
	private int findNextAliasInfoIndex(int curIndex) {
		return tableViewContainerMap.keySet()
				.stream()
				.filter(info -> info.getIndex() > curIndex)
				.sorted()
				.map(info -> info.getIndex())
				.findFirst()
				.orElse(-1);
	}

	public void prevContainer(ActionEvent e) {
		int curIndex = findAliasInfoIndex(aliasLabel.getText());
		int prevIndex = findPrevAliasInfoIndex(curIndex);
		if(prevIndex == -1) {
			prevIndex = findLastAliasInfoIndex();
		}
		showTableViewContainer(findAliasInfoByIndex(prevIndex).getAlias());
	}

	public void nextContainer(ActionEvent e) {
		int curIndex = findAliasInfoIndex(aliasLabel.getText());
		int nextIndex = findNextAliasInfoIndex(curIndex);
		if(nextIndex == -1) {
			nextIndex = findFirstAliasInfoIndex();
		}
		showTableViewContainer(findAliasInfoByIndex(nextIndex).getAlias());
	}

	@AllArgsConstructor
	@Data
	private static class AliasInfo implements Comparable<AliasInfo> {
		String alias;
		int index;

		@Override
		public int compareTo(AliasInfo o) {
			return this.index - o.index;
		}
	}
}
