package com.abdurrahman.latencyws.facades.impl;

import com.abdurrahman.latencyws.contants.LatencyConstants;
import com.abdurrahman.latencyws.dto.LatenciesResultDTO;
import com.abdurrahman.latencyws.dto.LatencyResultDTO;
import com.abdurrahman.latencyws.facades.LatencyFacade;
import com.abdurrahman.latencyws.model.LatencyData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
public class LatencyFacadeImpl implements LatencyFacade {

    private static final Logger logger = LoggerFactory.getLogger(LatencyFacadeImpl.class);

    @Autowired
    public RestTemplate restTemplate;

    @Override
    public LatenciesResultDTO getAllLatencies(String startDate, String endDate) {
        logger.debug("Entering Facade Implementation");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LatencyConstants.dateFormat);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {
            end.setTime(simpleDateFormat.parse(endDate));
            start.setTime(simpleDateFormat.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(end.compareTo(start)<0){
            logger.info("Date range is not correct");
            return new LatenciesResultDTO();
        }

        LatenciesResultDTO latenciesResultDTO = new LatenciesResultDTO();
        List<List<LatencyData>> latencyDataList = new ArrayList<>();

        StopWatch timeMeasure = new StopWatch();
        timeMeasure.start();
        performApiCalls(simpleDateFormat, start, end, latencyDataList);
        timeMeasure.stop();
        logger.info("Api Task took : {} s",timeMeasure.getTotalTimeSeconds());
        List<LatencyResultDTO> latencyResultData = prettify(latencyDataList);
        populateAnConvert(startDate, endDate, latenciesResultDTO, latencyResultData);

        return latenciesResultDTO;
    }

    private List<LatencyResultDTO> prettify(final List<List<LatencyData>> latencyDataList) {
        Map<Long, Long> serviceAndNumberOfUse = new ConcurrentHashMap<>();

        List<LatencyData> list = removeRedundancyRequestID(latencyDataList);

        for (LatencyData index:
                list) {
            serviceAndNumberOfUse.put(index.serviceId(), list.stream().filter(item -> item.serviceId() == index.serviceId()).count());

        }

        List<LatencyResultDTO> latencyResultData = new ArrayList<>();
        for(Map.Entry<Long,Long> s : serviceAndNumberOfUse.entrySet()){
            latencyResultData.add(new LatencyResultDTO(s.getKey().intValue(),s.getValue().intValue(),calculateAverage(list,s.getKey().intValue(),s.getValue().intValue())));
        }
        return latencyResultData;
    }

    private void populateAnConvert(String startDate, String endDate, LatenciesResultDTO latenciesResultDTO, List<LatencyResultDTO> latencyResultData) {
        latenciesResultDTO.setAverageLatencies(latencyResultData);
        String[] period = {startDate, endDate};
        latenciesResultDTO.setPeriod(period);
    }


    private void performApiCalls(final SimpleDateFormat simpleDateFormat, final Calendar start,final Calendar end, List<List<LatencyData>> latencyDataList) {
        while (end.compareTo(start)>=0){
                //latencyDataList.add(fetchLatencyAPIParallel(simpleDateFormat.format(start.getTime())).get());
                latencyDataList.add(fetchLatencyAPI(simpleDateFormat.format(start.getTime())));
                start.add(Calendar.DATE, 1);

        }
    }


    private List<LatencyData> fetchLatencyAPI(final String date){

        LatencyData[] latencyDataRecords = restTemplate.getForObject(LatencyConstants.urlApi+date, LatencyData[].class);
        if(latencyDataRecords.length==0){
            return null;
        }
        List<LatencyData> latencyDataRecordList = Arrays.asList(latencyDataRecords);

        return latencyDataRecordList;

    }

    private int calculateAverage(final List<LatencyData> list, final int serviceId, final int total){
        int avgUsingSum = list.stream().filter(filter -> filter.serviceId()==serviceId).mapToInt(a->a.milliSecondsDelay()).sum()/total;
        logger.debug("avgUsingSum is : {} and avg : {}",avgUsingSum,avgUsingSum);
        return avgUsingSum;
    }

    @Async
    public CompletableFuture<List<LatencyData>> fetchLatencyAPIParallel(final String date){

        LatencyData[] latencyDataRecords = restTemplate.getForObject(LatencyConstants.urlApi+date, LatencyData[].class);
        if(latencyDataRecords.length==0){
            return null;
        }
        List<LatencyData> latencyDataRecordList = Arrays.asList(latencyDataRecords);
        return CompletableFuture.completedFuture(latencyDataRecordList);

    }

    private List<LatencyData> removeRedundancyRequestID(final List<List<LatencyData>> list) {
        //Convert to one stream
        List<LatencyData> oneStream = list
                .parallelStream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        if(list.isEmpty()) return null;
        //removeRequestID

        Map<Integer, LatencyData> integerLatencyDataRecordMap = new HashMap<>();

        for (LatencyData index:
                oneStream) {
            integerLatencyDataRecordMap.put(index.requestId(),index);
        }

        return new ArrayList<>(integerLatencyDataRecordMap.values());
    }
}
