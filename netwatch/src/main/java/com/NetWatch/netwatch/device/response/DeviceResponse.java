package com.NetWatch.netwatch.device.response;

import com.NetWatch.netwatch.device.model.Device;
import com.NetWatch.netwatch.device.status.DeviceStatus;
import com.NetWatch.netwatch.device.status.DeviceType;

import java.time.Instant;

public record DeviceResponse(
        Long id, String name, String ipAddress, DeviceType type,
        boolean active, DeviceStatus status, Double lastLatencyMs, Instant lastCheckedAt) {

    public static DeviceResponse from(Device d) {

        return new DeviceResponse(d.getId(), d.getName(),
                d.getIpAddress(), d.getType(), d.isActive(), d.getCurrenteStatus(),
                d.getLastLatencyMs(), d.getLastCheckedAt());
    }
}
