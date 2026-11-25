package com.NetWatch.netwatch.check;

import com.NetWatch.netwatch.device.model.Device;
import com.NetWatch.netwatch.device.status.DeviceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "check_result")
@Getter
@Setter
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DeviceStatus status;

    @Column(name = "latency_ms")
    private Double latencyMs;

    @Column(length = 255)
    private String message;

    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;

    protected CheckResult() { }

    public CheckResult(Device device, DeviceStatus status, Double latencyMs,
                       String message, Instant checkedAt) {
        this.device = device;
        this.status = status;
        this.latencyMs = latencyMs;
        this.message = message;
        this.checkedAt = checkedAt;
    }
}
