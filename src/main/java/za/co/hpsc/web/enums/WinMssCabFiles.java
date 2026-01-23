package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum WinMssCabFiles {
    MATCH("THEMATCH.XML"),
    STAGE("STAGE.XML"),
    TAG("TAG.XML"),
    ENROLLED("ENROLLED.XML"),
    MEMBER("MEMBER.XML"),
    SQUAD("SQUAD.XML"),
    SCORE("SCORE.XML");

    private final String fileName;

    public Optional<WinMssCabFiles> getByFileName(String fileName) {
        if ((fileName == null) || (fileName.isBlank())) {
            return Optional.empty();
        }

        return Arrays.stream(WinMssCabFiles.values())
                .filter(cabFile -> cabFile.getFileName().equalsIgnoreCase(fileName))
                .findFirst();
    }
}
