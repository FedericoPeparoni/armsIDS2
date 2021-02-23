package ca.ids.abms.modules.util.models;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;

/**
 * Created by c.talpa on 01/03/2017.
 */
public class DateTimeUtilsTest {

    @Test
    public void testPlusMinutes() {
        //Test Case 1
        String depTime="1202";
        Integer depRange=10;
        String trueValue="1212";
        String result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 2
        depTime="0002";
        depRange=12;
        trueValue="0014";
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 3
        depTime="0001";
        depRange=59;
        trueValue="0100";
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 4
        depTime="1255";
        depRange=125;
        trueValue="1500";
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 5
        depTime="1255";
        depRange=0;
        trueValue="1255";
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 6
        depTime="1255";
        depRange=720;
        trueValue="0055";
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 7
        depTime="    ";
        depRange=720;
        trueValue=null;
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);
    }


    @Test
    public void testMinusMinutes() {
        //Test Case 1
        String depTime="1212";
        Integer depRange=10;
        String trueValue="1202";
        String result= DateTimeUtils.minusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 2
        depTime="0002";
        depRange=12;
        trueValue="2350";
        result= DateTimeUtils.minusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 3
        depTime="0001";
        depRange=59;
        trueValue="2302";
        result= DateTimeUtils.minusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 4
        depTime="1205";
        depRange=125;
        trueValue="1000";
        result= DateTimeUtils.minusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 5
        depTime="1255";
        depRange=0;
        trueValue="1255";
        result= DateTimeUtils.minusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 6
        depTime="1155";
        depRange=720;
        trueValue="2355";
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);

        //Test Case 7
        depTime="    ";
        depRange=720;
        trueValue=null;
        result= DateTimeUtils.plusMinutes(depTime,depRange);
        assertEquals(trueValue,result);
    }

    @Test
    public void testLocalDateTimeMinusMinutes() {
        //Test Case 1
        LocalDateTime localDateTime=LocalDateTime.of(2017,3,1,0,0);
        String depTime="1212";
        Integer depRange=10;
        LocalDateTime trueValue=LocalDateTime.of(2017,3,1,12,2);
        LocalDateTime localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 2
        localDateTime=LocalDateTime.of(2017,3,2,0,0);
        depTime="0002";
        depRange=12;
        trueValue=LocalDateTime.of(2017,3,1,23,50);
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 3
        localDateTime=LocalDateTime.of(2017,3,2,0,0);
        depTime="0001";
        depRange=59;
        trueValue=LocalDateTime.of(2017,3,1,23,2);
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 4
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="1205";
        depRange=125;
        trueValue=LocalDateTime.of(2017,3,1,10,0);
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 5
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="1205";
        depRange=0;
        trueValue=LocalDateTime.of(2017,3,1,12,5);
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 6
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="1155";
        depRange=720;
        trueValue=LocalDateTime.of(2017,2,28,23,55);
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 7
        localDateTime=LocalDateTime.of(2017,1,1,0,0);
        depTime="1155";
        depRange=720;
        trueValue=LocalDateTime.of(2016,12,31,23,55);
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 8
        localDateTime=null;
        depTime="    ";
        depRange=720;
        trueValue=null;
        localDateTimeResult= DateTimeUtils.minusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

    }

    @Test
    public void testLocalDateTimePlusMinutes() {
        //Test Case 1
        LocalDateTime localDateTime=LocalDateTime.of(2017,3,1,0,0);
        String depTime="1212";
        Integer depRange=10;
        LocalDateTime trueValue=LocalDateTime.of(2017,3,1,12,22);
        LocalDateTime localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 2
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="2352";
        depRange=12;
        trueValue=LocalDateTime.of(2017,3,2,0,4);
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 3
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="2301";
        depRange=59;
        trueValue=LocalDateTime.of(2017,3,2,0,0);
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 4
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="1205";
        depRange=125;
        trueValue=LocalDateTime.of(2017,3,1,14,10);
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 5
        localDateTime=LocalDateTime.of(2017,3,1,0,0);
        depTime="1205";
        depRange=0;
        trueValue=LocalDateTime.of(2017,3,1,12,5);
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 6
        localDateTime=LocalDateTime.of(2017,2,28,0,0);
        depTime="2355";
        depRange=720;
        trueValue=LocalDateTime.of(2017,3,1,11,55);
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);


        //Test Case 7
        localDateTime=LocalDateTime.of(2016,12,31,0,0);
        depTime="2355";
        depRange=720;
        trueValue=LocalDateTime.of(2017,1,1,11,55);
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);

        //Test Case 8
        localDateTime=null;
        depTime="    ";
        depRange=720;
        trueValue=null;
        localDateTimeResult= DateTimeUtils.plusMinute(localDateTime,depTime,depRange);
        assertEquals(trueValue,localDateTimeResult);
    }

    @Test
    public void testGetTime(){
        //Test Case 1
        String estimatedElapsedTime="0120";
        Integer trueValue=80;
        Integer minutes= DateTimeUtils.getMinutes(estimatedElapsedTime);
        assertEquals(trueValue,minutes);

        //Test Case 2
        estimatedElapsedTime="0000";
        trueValue=0;
        minutes= DateTimeUtils.getMinutes(estimatedElapsedTime);
        assertEquals(trueValue,minutes);

        //Test Case 3
        estimatedElapsedTime=" ";
        trueValue=0;
        minutes= DateTimeUtils.getMinutes(estimatedElapsedTime);
        assertEquals(trueValue,minutes);

        //Test Case 4
        estimatedElapsedTime=null;
        trueValue=0;
        minutes= DateTimeUtils.getMinutes(estimatedElapsedTime);
        assertEquals(trueValue,minutes);


        //Test Case 5
        estimatedElapsedTime="0234";
        trueValue=154;
        minutes= DateTimeUtils.getMinutes(estimatedElapsedTime);
        assertEquals(trueValue,minutes);

        //Test Case 6
        estimatedElapsedTime="0165";
        trueValue=0;
        minutes = 0;
        try {
            minutes= DateTimeUtils.getMinutes(estimatedElapsedTime);
        } catch (DateTimeParseException  drpe) {
            
        }
        assertEquals(trueValue,minutes);
    }

}
