package root.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReleaseInfo {
    private String appName;
    private String releaseVersion;
    private LocalDateTime releaseDate;
    private String downloadLink;
    private String releaseNoteLink;
}
