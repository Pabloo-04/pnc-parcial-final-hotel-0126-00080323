package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.model.enums.RoomType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private RoomType type;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private Boolean available;
    private Long hotelId;
    private String hotelName;
}