package com.virnect.smic.common.util;

import org.apache.commons.lang3.time.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StopWatchUtil {
    public static StopWatch stopWatch = new StopWatch();

    public static void delay(long delayMilliSeconds)  {
        try{
            Thread.sleep(delayMilliSeconds);
        }catch (Exception e){
            log.error("Exception is :" + e.getMessage());
        }

    }

    public static String transForm(String s) {
        StopWatchUtil.delay(500);
        return s.toUpperCase();
    }

    public static void startTimer(){
        stopWatch.start();
    }

    public static void timeTaken(){
        stopWatch.stop();
        log.info("Total Time Taken : " +stopWatch.getTime());
    }

    public static void stopWatchReset(){
        stopWatch.reset();
    }

    public static  int noOfCores(){
        return Runtime.getRuntime().availableProcessors();
    }
}
