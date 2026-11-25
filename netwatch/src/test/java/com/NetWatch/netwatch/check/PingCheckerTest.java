package com.NetWatch.netwatch.check;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PingCheckerTest {

    @Test
    void parseiaLatenciaLinux() {
        assertEquals(12.3, PingChecker.parseLatency("64 bytes from 8.8.8.8: icmp_seq=1 ttl=117 time=12.3 ms"));
    }

    @Test
    void parseiaLatenciaWindowsPortugues() {
        assertEquals(15.0, PingChecker.parseLatency("Resposta de 8.8.8.8: bytes=32 tempo=15ms TTL=117"));
    }

    @Test
    void parseiaLatenciaMenorQueUmMs() {
        assertEquals(0.5, PingChecker.parseLatency("Reply from 192.168.0.1: bytes=32 time<1ms TTL=64"));
    }

    @Test
    void semLatenciaRetornaNulo() {
        assertNull(PingChecker.parseLatency("Request timed out."));
    }
}