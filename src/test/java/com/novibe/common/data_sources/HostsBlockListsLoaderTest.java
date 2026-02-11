package com.novibe.common.data_sources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class HostsBlockListsLoaderTest {

    private HostsBlockListsLoader loader;

    @BeforeEach
    void setUp() {
        loader = new HostsBlockListsLoader();
    }

    @Test
    void testIsBlock() {
        assertTrue(HostsBlockListsLoader.isBlock("0.0.0.0 example.com"));
        assertTrue(HostsBlockListsLoader.isBlock("127.0.0.1 example.com"));
        assertTrue(HostsBlockListsLoader.isBlock("::1 example.com"));
        assertFalse(HostsBlockListsLoader.isBlock("1.2.3.4 example.com"));
        assertFalse(HostsBlockListsLoader.isBlock("# comment"));
    }

    @Test
    void testFilterRelatedLines() {
        Predicate<String> filter = loader.filterRelatedLines();

        assertTrue(filter.test("0.0.0.0 example.com"));
        assertFalse(filter.test("0.0.0.0 localhost"));
        assertFalse(filter.test("127.0.0.1 ip6-localhost"));
        assertFalse(filter.test("1.2.3.4 example.com"));
    }

    @Test
    void testToObject() {
        assertEquals("example.com", loader.toObject("0.0.0.0 example.com"));
        assertEquals("example.com", loader.toObject("0.0.0.0 www.example.com"));
        assertEquals("google.com", loader.toObject("127.0.0.1 www.google.com"));
        assertEquals("ipv6.test", loader.toObject("::1 ipv6.test"));
    }
}
