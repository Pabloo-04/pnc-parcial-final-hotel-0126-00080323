package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.model.enums.ReservationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long roomId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private ReservationStatus status;
    private BigDecimal totalPrice;
}