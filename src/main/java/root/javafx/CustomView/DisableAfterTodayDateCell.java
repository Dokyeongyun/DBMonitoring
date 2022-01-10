package root.javafx.CustomView;

import java.time.LocalDate;

import javafx.scene.control.DateCell;

public class DisableAfterTodayDateCell extends DateCell {
	
	@Override
	public void updateItem(LocalDate item, boolean empty) {
		super.updateItem(item, empty);
        LocalDate today = LocalDate.now();
        setDisable(empty || item.compareTo(today) > 0 );
	}
}
