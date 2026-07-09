package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByRoomHotelId(Long hotelId);
}