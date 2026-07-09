package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.HotelRequest;
import com.uca.pncparcialfinalhotel.dto.response.HotelResponse;

import java.util.List;

public interface HotelService {

    HotelResponse create(HotelRequest request);

    HotelResponse getById(Long id);

    List<HotelResponse> getAll();

    HotelResponse update(Long id, HotelRequest request);

    void delete(Long id);
}