package com.budgetapp.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void extractTransactionFindsAmountDateMerchantAndCategory() {
        parser p = new parser();
        parser.ParsedTransaction result = p.extractTransaction("You spent $18.40 at Uber on 2026-05-02");

        assertNotNull(result);
        assertEquals(18.40, result.amount, 0.001);
        assertEquals("2026-05-02", result.date);
        assertEquals("Uber", result.merchant);
        assertEquals("Transport", result.category);
    }

    @Test
    void extractTransactionReturnsNullWhenAmountMissing() {
        parser p = new parser();
        parser.ParsedTransaction result = p.extractTransaction("You made a purchase at Starbucks on 2026-05-01");

        assertNull(result);
    }
}
