package com.abdurrahman.latencyws.model;

public record LatencyData(int requestId, long serviceId, String date, int milliSecondsDelay) {
}
