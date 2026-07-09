package com.uca.pncparcialfinalhotel.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String description;
}