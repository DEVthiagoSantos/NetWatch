package com.NetWatch.netwatch.check;

/** Resultado bruto de uma verificação, independente do protocolo usado. */
public record CheckOutcome(
        boolean reachable, Double latencyMs,
        String message
) {
}
