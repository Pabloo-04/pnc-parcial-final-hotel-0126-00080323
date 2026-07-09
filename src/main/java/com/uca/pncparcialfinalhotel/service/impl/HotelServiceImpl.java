package com.uca.pncparcialfinalhotel.service.impl;

import com.uca.pncparcialfinalhotel.dto.request.HotelRequest;
import com.uca.pncparcialfinalhotel.dto.response.HotelResponse;
import com.uca.pncparcialfinalhotel.exception.ForbiddenException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.mapper.HotelMapper;
import com.uca.pncparcialfinalhotel.model.Hotel;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.security.SecurityUtils;
import com.uca.pncparcialfinalhotel.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final SecurityUtils securityUtils;

    @Override
    public HotelResponse create(HotelRequest request) {
        Hotel hotel = hotelMapper.toEntity(request);
        return hotelMapper.toResponse(hotelRepository.save(hotel));
    }

    @Override
    public HotelResponse getById(Long id) {
        if (securityUtils.isReceptionist()) {
            Long assignedHotelId = securityUtils.requireReceptionistHotelId();
            if (!id.equals(assignedHotelId)) {
                throw new ForbiddenException("Receptionists can only access their assigned hotel");
            }
        }
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return hotelMapper.toResponse(hotel);
    }

    @Override
    public List<HotelResponse> getAll() {
        return hotelRepository.findAll().stream()
                .map(hotelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HotelResponse update(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        hotelMapper.updateEntity(hotel, request);
        return hotelMapper.toResponse(hotelRepository.save(hotel));
    }

    @Override
    public void delete(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel not found with id: " + id);
        }
        hotelRepository.deleteById(id);
    }
}