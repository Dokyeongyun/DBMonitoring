package root.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import root.model.ReleaseInfo;

@Controller
public class HomeHandler {

    @GetMapping(path = "/")
    public String home(Model model) {

        model.addAttribute("title", "Project DBMonitoring");

        Calendar cal = Calendar.getInstance();
        cal.set(2022, Calendar.JUNE, 1);
        ReleaseInfo r1 = new ReleaseInfo();
        r1.setAppName("DBMonitoring");
        r1.setReleaseVersion("1.0.0");
        r1.setReleaseDate(LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault()));
        r1.setDownloadLink("https://github.com/Dokyeongyun/DBMonitoring/releases/download/v1.0.0/DBMonitoring-1.0.0.zip");
        r1.setReleaseNoteLink("https://github.com/Dokyeongyun/DBMonitoring/releases/tag/v1.0.0");

        List<ReleaseInfo> releaseList = new ArrayList<>();
        releaseList.add(r1);

        model.addAttribute("releaseList", releaseList);
        return "index";
    }
}
