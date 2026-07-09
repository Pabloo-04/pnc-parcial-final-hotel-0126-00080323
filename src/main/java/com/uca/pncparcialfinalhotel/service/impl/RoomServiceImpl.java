package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.dto.request.RoomRequest;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponse;
import com.uca.pncparcialfinalhotel.exception.ForbiddenException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.mapper.RoomMapper;
import com.uca.pncparcialfinalhotel.model.Hotel;
import com.uca.pncparcialfinalhotel.model.Room;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import com.uca.pncparcialfinalhotel.security.SecurityUtils;
import com.uca.pncparcialfinalhotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;
    private final SecurityUtils securityUtils;

    @Override
    public RoomResponse create(RoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + request.getHotelId()));
        Room room = roomMapper.toEntity(request, hotel);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse getById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        if (securityUtils.isReceptionist()) {
            Long assignedHotelId = securityUtils.requireReceptionistHotelId();
            if (!room.getHotel().getId().equals(assignedHotelId)) {
                throw new ForbiddenException("Receptionists can only access rooms from their assigned hotel");
            }
        }
        return roomMapper.toResponse(room);
    }

    @Override
    public List<RoomResponse> getAll() {
        if (securityUtils.isReceptionist()) {
            Long assignedHotelId = securityUtils.requireReceptionistHotelId();
            return roomRepository.findByHotelId(assignedHotelId).stream()
                    .map(roomMapper::toResponse)
                    .collect(Collectors.toList());
        }
        return roomRepository.findAll().stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomResponse> getByHotelId(Long hotelId) {
        if (securityUtils.isReceptionist()) {
            Long assignedHotelId = securityUtils.requireReceptionistHotelId();
            if (!hotelId.equals(assignedHotelId)) {
                throw new ForbiddenException("Receptionists can only access rooms from their assigned hotel");
            }
        }
        return roomRepository.findByHotelId(hotelId).stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + request.getHotelId()));
        roomMapper.updateEntity(room, request, hotel);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }
}