package com.example.seg2105_d1.Model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TutorTest {

    @Test
    public void testUpdateRating() {
        Tutor tutor = new Tutor();
        tutor.updateRating(4);
        tutor.updateRating(3);
        tutor.updateRating(1);
        tutor.updateRating(5);
        tutor.updateRating(5);
        tutor.updateRating(4);
        assertEquals(3.666,tutor.getRating(),0.01);
    }

}