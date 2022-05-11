package root.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class AppConfiguration {
	
	@Autowired
	private ApplicationContext context;

	@Autowired
	private Environment env;

	@PostConstruct 
	public void init() {
	}
}
