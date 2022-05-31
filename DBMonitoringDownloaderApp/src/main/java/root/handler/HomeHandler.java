package root.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeHandler {

	@GetMapping(path = "/")
	public String home(Model model) {
		
		model.addAttribute("title", "Home");
		
		List<String> downloadLinks = new ArrayList<>();
		downloadLinks.add("https://DBMonitoring.co.kr/download/release-1.0.0");
		downloadLinks.add("https://DBMonitoring.co.kr/download/release-1.0.1");
		downloadLinks.add("https://DBMonitoring.co.kr/download/release-1.1.0");
		downloadLinks.add("https://DBMonitoring.co.kr/download/release-1.1.1");
		model.addAttribute("downloadLinks", downloadLinks);
		return "index";
	}
}
