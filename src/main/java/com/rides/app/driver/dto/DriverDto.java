package com.rides.app.driver.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String licenseNumber;
    private Boolean isActive;
}
