package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}