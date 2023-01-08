package com.abdurrahman.latencyws.model;

import com.abdurrahman.latencyws.dto.LatencyResultDTO;

import java.util.List;
public record LatenciesResultData(String[] period, List<LatencyResultDTO> averageLatencies) {
}
