package com.NetWatch.netwatch.device.repository;

import com.NetWatch.netwatch.device.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    boolean existsByIpAddress(String ipAddress);
    boolean existsByIpAddressAndIdNot(String ipAddress, Long id);
    List<Device> findByActiveTrue();
}
