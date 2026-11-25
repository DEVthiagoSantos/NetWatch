package com.NetWatch.netwatch.check;

import com.NetWatch.netwatch.device.status.DeviceStatus;

import java.time.Instant;

public record CheckResultResponse(
        Long id, Long deviceId, DeviceStatus status,
        Double latencyMs, String message, Instant checkedAt
) {

    public static CheckResultResponse from(CheckResult r) {
        return new CheckResultResponse(r.getId(), r.getDevice().getId(),
                r.getStatus(), r.getLatencyMs(), r.getMessage(), r.getCheckedAt());
    }
}
