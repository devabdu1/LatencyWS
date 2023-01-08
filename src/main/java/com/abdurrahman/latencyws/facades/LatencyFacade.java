package com.abdurrahman.latencyws.facades;


import com.abdurrahman.latencyws.dto.LatenciesResultDTO;

public interface LatencyFacade {

    LatenciesResultDTO getAllLatencies(String startDate, String endDate);
}
