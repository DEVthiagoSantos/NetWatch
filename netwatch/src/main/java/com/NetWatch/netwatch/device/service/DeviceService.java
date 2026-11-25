package com.NetWatch.netwatch.device.service;

import com.NetWatch.netwatch.common.ConflictException;
import com.NetWatch.netwatch.common.NotFoundException;
import com.NetWatch.netwatch.device.response.DeviceRequest;
import com.NetWatch.netwatch.device.model.Device;
import com.NetWatch.netwatch.device.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Device> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Device getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dispositivo %d não encontrado".formatted(id)));
    }

    @Transactional
    public Device create(DeviceRequest req) {
        if (repository.existsByIpAddress(req.ipAddress())) {
            throw new ConflictException("Já existe um dispositivo com o IP " + req.ipAddress());
        }
        Device d = new Device();
        apply(d, req);
        return repository.save(d);
    }

    @Transactional
    public Device update(Long id, DeviceRequest req) {
        Device d = getById(id);
        if (repository.existsByIpAddressAndIdNot(req.ipAddress(), id)) {
            throw new ConflictException("Já existe um dispositivo com o IP " + req.ipAddress());
        }
        apply(d, req);
        return d;

    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getById(id));
    }

    private void apply(Device d, DeviceRequest req) {
        d.setName(req.name().trim());
        d.setIpAddress(req.ipAddress());
        d.setType(req.type());
        if (req.active() != null) {
            d.setActive(req.active());
        }
    }
}
