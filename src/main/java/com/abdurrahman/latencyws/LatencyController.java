package com.abdurrahman.latencyws;

import com.abdurrahman.latencyws.dto.LatenciesResultDTO;
import com.abdurrahman.latencyws.service.impl.LatencyApiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class LatencyController {
    private static final Logger logger = LoggerFactory.getLogger(LatencyController.class);


    private final LatencyApiService latencyApiService;

    @Autowired
    private LatencyController(LatencyApiService latencyApiService){
        this.latencyApiService = latencyApiService;
    }


    @GetMapping(value="/latencies",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LatenciesResultDTO getLatencies1(@NonNull @RequestParam("startDate") final String startDate,
                                           @NonNull @RequestParam("endDate") final String endDate) {
        logger.info("this is /latences endpoint");
        return latencyApiService.latenciesService(startDate,endDate);
    }

}
