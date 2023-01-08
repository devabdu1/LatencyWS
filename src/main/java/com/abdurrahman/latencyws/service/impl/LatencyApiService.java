package com.abdurrahman.latencyws.service.impl;

import com.abdurrahman.latencyws.contants.LatencyConstants;
import com.abdurrahman.latencyws.dto.LatenciesResultDTO;
import com.abdurrahman.latencyws.facades.impl.LatencyFacadeImpl;
import com.abdurrahman.latencyws.model.LatencyData;
import com.abdurrahman.latencyws.service.LatencyService;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class LatencyApiService implements LatencyService {

    private static final Logger logger = LoggerFactory.getLogger(LatencyApiService.class);
    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private LatencyFacadeImpl latencyFacade;


    @Override
    public List<LatencyData> fetchLatencies(final String date) throws JsonProcessingException{
        logger.debug("Starting fetching for the date : {}",date);
        LatencyData[] latencyData = restTemplate.getForObject(LatencyConstants.urlApi+date, LatencyData[].class);
        logger.debug("Finishing fetching for the date : {}",date);
        return Arrays.asList(latencyData);
    }

    @Override
    public LatenciesResultDTO latenciesService(final String startDate, final String endDate) {

        return latencyFacade.getAllLatencies(startDate,endDate);

    }


}
