package com.NetWatch.netwatch.device.response;

import com.NetWatch.netwatch.device.status.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DeviceRequest(
        @NotBlank @Size(max = 100) String name,

        @NotBlank
        @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$",
        message = "deve ser um endereço IPv4 válido")
        String ipAddress,

        @NotNull DeviceType type,

        Boolean active) {
}
