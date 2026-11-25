package com.NetWatch.netwatch.check;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Verifica disponibilidade usando o utilitário "ping" do sistema operacional (ICMP real,
 * sem exigir privilégios de root na JVM). Funciona em Windows, Linux e macOS.
 *
 * O IP é sempre passado como argumento separadp (nunca via shell), e o DTO já valida IPv4,
 * o que elimina risco de command injection.
 */

@Component
public class PingChecker implements Checker {

    // Cobre "time=12.3 ms", "time<1ms" (Windows) e "tempo=12ms" (Windows em português)
    private static final Pattern LATENCY =
            Pattern.compile("(?i)(?:time|tempo)\\s*([=<])\\s*([\\d.,]+)\\s*ms");

    private static final String OS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    private static final boolean WINDOWS = OS.contains("win");
    private static final boolean MAC = OS.contains("mac");

    private final int timeoutMs;

    public PingChecker(@Value("${monitor.ping.timeout-ms:2000}") int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public CheckOutcome check(String host) {
        try {
            Process process = new ProcessBuilder(buildCommand(host))
                    .redirectErrorStream(true)
                    .start();

            String output;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            if (!process.waitFor(timeoutMs + 3000L, TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                return new CheckOutcome(false, null, "Tempo esgotado ao executar o ping");
            }

            Double latency = parseLatency(output);
            boolean hasReply = output.toLowerCase(Locale.ROOT).contains("ttl");
            boolean reachable = process.exitValue() == 0 && (latency != null || hasReply);

            if (reachable) {
                return new CheckOutcome(true, latency, "Resposta recebida");
            }
            return new CheckOutcome(false, null, "Sem resposta ao ping");
        } catch (IOException e) {
            return new CheckOutcome(false, null, "Falha ao executar o ping: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CheckOutcome(false, null, "Verificação interrompida");
        }
    }

    private List<String> buildCommand(String host) {
        if (WINDOWS) {
            return List.of("ping", "-n", "1", "-w", String.valueOf(timeoutMs), host);
        }
        if (MAC) {
            return List.of("ping", "-c", "1", "-W", String.valueOf(timeoutMs), host);
        }
        int seconds = Math.max(1, (int) Math.ceil(timeoutMs / 1000.0));
        return List.of("ping", "-c", "1", "W", String.valueOf(seconds), host);
    }

    static Double parseLatency(String output) {
        Matcher m = LATENCY.matcher(output);
        if (!m.find()) {
            return null;
        }
        if ("<".equals(m.group(1))) {
            return 0.5; //"time<1ms"
        }
        try {
            return Double.parseDouble(m.group(2).replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}