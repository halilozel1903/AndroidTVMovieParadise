package com.halil.ozel.movieparadise.data.models;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;

import org.junit.Test;

public class MovieDetailsParsingTest {

    @Test
    public void parsesBudgetAndRevenueLargerThanAnInt() {
        // Blockbusters gross more than Integer.MAX_VALUE (2,147,483,647) dollars.
        String json = "{\"id\":19995,\"title\":\"Avatar\",\"runtime\":162,"
                + "\"budget\":237000000,\"revenue\":2923706026}";

        MovieDetails details = new Gson().fromJson(json, MovieDetails.class);

        assertEquals("Avatar", details.getTitle());
        assertEquals(237_000_000L, details.getBudget());
        assertEquals(2_923_706_026L, details.getRevenue());
    }
}
