package com.abdurrahman.latencyws.dto;

import lombok.Data;

import java.util.List;

@Data
public class LatenciesResultDTO {
    public String[] period;
    public List<LatencyResultDTO> averageLatencies;
}
