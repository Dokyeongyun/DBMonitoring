package root.javafx.Service;

import javafx.concurrent.Service;

public abstract class ConnectionTestService extends Service<Boolean> {
	
	public abstract void alertSucceed();
	
	public abstract void alertFailed();
}
