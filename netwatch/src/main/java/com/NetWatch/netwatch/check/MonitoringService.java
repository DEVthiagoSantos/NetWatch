package com.NetWatch.netwatch.check;

import java.time.Instant;
import java.util.List;

import com.NetWatch.netwatch.device.model.Device;
import com.NetWatch.netwatch.device.repository.DeviceRepository;
import com.NetWatch.netwatch.device.service.DeviceService;
import com.NetWatch.netwatch.device.status.DeviceStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MonitoringService {

    private final DeviceService deviceService;
    private final DeviceRepository deviceRepository;
    private final CheckResultRepository resultRepository;
    private final Checker pingChecker;
    private final double warningLatencyMs;

    public MonitoringService(DeviceService deviceService,
                             DeviceRepository deviceRepository,
                             CheckResultRepository resultRepository,
                             PingChecker pingChecker,
                             @Value("${monitor.warning-latency-ms:200}") double warningLatencyMs) {
        this.deviceService = deviceService;
        this.deviceRepository = deviceRepository;
        this.resultRepository = resultRepository;
        this.pingChecker = pingChecker;
        this.warningLatencyMs = warningLatencyMs;
    }

    /** Verifica um dispositivo agora e registra o resultado com data/hora. */
    @Transactional
    public CheckResult checkNow(Long deviceId) {
        return checkDevice(deviceService.getById(deviceId));
    }

    /** Verifica todos os dispositivos ativos (sequencial na Etapa 1; paralelo na Etapa 2). */
    @Transactional
    public List<CheckResult> checkAllActive() {
        return deviceRepository.findByActiveTrue().stream().map(this::checkDevice).toList();
    }

    @Transactional(readOnly = true)
    public List<CheckResult> history(Long deviceId, int size) {
        deviceService.getById(deviceId); // garante 404 se não existir
        int limit = Math.min(Math.max(size, 1), 500);
        return resultRepository.findByDeviceIdOrderByCheckedAtDesc(deviceId, PageRequest.of(0, limit)).getContent();
    }

    private CheckResult checkDevice(Device device) {
        Instant checkedAt = Instant.now();
        CheckOutcome outcome = pingChecker.check(device.getIpAddress());

        DeviceStatus status;
        if (!outcome.reachable()) {
            status = DeviceStatus.OFFLINE;
        } else if (outcome.latencyMs() != null && outcome.latencyMs() > warningLatencyMs) {
            status = DeviceStatus.WARNING;
        } else {
            status = DeviceStatus.ONLINE;
        }

        device.setCurrenteStatus(status);
        device.setLastLatencyMs(outcome.latencyMs());
        device.setLastCheckedAt(checkedAt);
        deviceRepository.save(device);

        return resultRepository.save(
                new CheckResult(device, status, outcome.latencyMs(), outcome.message(), checkedAt));
    }
}