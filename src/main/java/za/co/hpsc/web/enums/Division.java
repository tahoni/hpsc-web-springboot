package za.co.hpsc.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Division {
    HANDGUN(List.of("Handgun")),
    HANDGUN_22(List.of(".22LR Handgun", ".22LR", ".22")),
    SHOTGUN(List.of("Shotgun")),
    RIFLE(List.of("Rifle")),
    MINI_RIFLE(List.of("Mini-Rifle")),
    PCC(List.of("PCC", "Pistol Caliber Carbine"));

    private final List<String> names;
}
