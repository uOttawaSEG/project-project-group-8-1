package com.example.seg2105_d1.Model;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityTest {

    LocalDate date1 = LocalDate.parse("2025-11-25");
    LocalDate date2 = LocalDate.parse("2025-11-24");
    LocalTime time1 = LocalTime.parse("06:30");
    LocalTime time2 = LocalTime.parse("07:30");

    LocalTime time3 = LocalTime.parse("07:00");

    @Test
    public void testOverlapTrue() {
        assertTrue(Availability.Overlap(date1,date1,time1,time2,time3,time2));
    }

    @Test
    public void testOverlapFalseDifferentDaySameTime() {
        assertFalse(Availability.Overlap(date1,date2,time1,time2,time3,time2));
    }

    @Test
    public void testOverlapFalseSameDayDifferentTime() {
        assertFalse(Availability.Overlap(date1,date1,time1,time3,time3,time2));
    }

}