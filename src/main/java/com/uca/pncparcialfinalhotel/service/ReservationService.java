package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.ReservationRequest;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponse;
import com.uca.pncparcialfinalhotel.model.enums.ReservationStatus;

import java.util.List;

public interface ReservationService {

    ReservationResponse create(ReservationRequest request);

    ReservationResponse getById(Long id);

    List<ReservationResponse> getAll();

    List<ReservationResponse> getByUserId(Long userId);

    ReservationResponse update(Long id, ReservationRequest request);

    ReservationResponse updateStatus(Long id, ReservationStatus status);

    void delete(Long id);
}