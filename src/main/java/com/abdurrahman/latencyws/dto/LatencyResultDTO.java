package com.abdurrahman.latencyws.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;


@Getter
@AllArgsConstructor
@Builder
public class LatencyResultDTO implements Serializable {
    public int serviceId;
    public int numberOfRequests;
    public int averageResonseTimeMs;

}
