package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.dto.request.ReservationRequest;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponse;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.mapper.ReservationMapper;
import com.uca.pncparcialfinalhotel.model.Reservation;
import com.uca.pncparcialfinalhotel.model.Room;
import com.uca.pncparcialfinalhotel.model.User;
import com.uca.pncparcialfinalhotel.model.enums.ReservationStatus;
import com.uca.pncparcialfinalhotel.repository.ReservationRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import com.uca.pncparcialfinalhotel.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        validateDates(request);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));
        if (!room.getAvailable()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is not available");
        }
        room.setAvailable(false);
        roomRepository.save(room);
        Reservation reservation = reservationMapper.toEntity(request, user, room);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse getById(Long id) {
        return reservationMapper.toResponse(
                reservationRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id))
        );
    }

    @Override
    public List<ReservationResponse> getAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationResponse> getByUserId(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse update(Long id, ReservationRequest request) {
        validateDates(request);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + request.getRoomId()));

        if (!reservation.getRoom().getId().equals(room.getId())) {
            reservation.getRoom().setAvailable(true);
            roomRepository.save(reservation.getRoom());
            if (!room.getAvailable()) {
                throw new IllegalStateException("Room " + room.getRoomNumber() + " is not available");
            }
            room.setAvailable(false);
            roomRepository.save(room);
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setTotalPrice(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        if (status == ReservationStatus.CANCELLED && reservation.getStatus() != ReservationStatus.CANCELLED) {
            reservation.getRoom().setAvailable(true);
            roomRepository.save(reservation.getRoom());
        }
        reservation.setStatus(status);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public void delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        if (reservation.getStatus() != ReservationStatus.CANCELLED) {
            reservation.getRoom().setAvailable(true);
            roomRepository.save(reservation.getRoom());
        }
        reservationRepository.deleteById(id);
    }

    private void validateDates(ReservationRequest request) {
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}