package com.github.hanielcota.homes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Home {

    private String playerName;
    private String homeName;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;
}
