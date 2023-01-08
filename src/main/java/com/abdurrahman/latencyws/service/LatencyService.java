package com.abdurrahman.latencyws.service;

import com.abdurrahman.latencyws.dto.LatenciesResultDTO;
import com.abdurrahman.latencyws.model.LatencyData;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;


public interface LatencyService {

    List<LatencyData> fetchLatencies(final String date) throws JsonProcessingException;
    LatenciesResultDTO latenciesService(final String startDate, final String endDate);
}
