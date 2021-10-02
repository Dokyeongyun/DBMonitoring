package Root.Usecases;

import Root.Model.AlertLogCommand;

public interface ServerCheckUsecase {
	void printAlertLog(AlertLogCommand alc);
	void printOSDiskUsage(String command);
}
