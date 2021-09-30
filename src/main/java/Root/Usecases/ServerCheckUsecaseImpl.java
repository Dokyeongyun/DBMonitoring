package Root.Usecases;

import Root.Repository.ServerCheckRepository;

public class ServerCheckUsecaseImpl implements ServerCheckUsecase {
	private ServerCheckRepository serverCheckRepository;

	public ServerCheckUsecaseImpl(ServerCheckRepository serverCheckRepository) {
		this.serverCheckRepository = serverCheckRepository;
	}

	@Override
	public void printAlertLog() {
		String result = serverCheckRepository.checkAlertLog();
		System.out.println(result);		
	}
}
