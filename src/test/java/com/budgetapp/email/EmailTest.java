package com.budgetapp.email;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import jakarta.mail.search.SearchTerm;

/**
 * These verify that the filter builder works.
 * No live connection needed
 */
public class EmailTest {
    @Test
    void filterMessagesTest()  {
        SearchTerm result = email.filterMessages("alerts@td.com");
        assertNotNull(result);
    }

    @Test
    void filterMessagesWithSubjectTest()  {
        SearchTerm result = email.filterMessages("alerts@td.com", "transaction");
        assertNotNull(result);
    }
}
