package com.NetWatch.netwatch.device.controller;

import com.NetWatch.netwatch.device.model.Device;
import com.NetWatch.netwatch.device.response.DeviceRequest;
import com.NetWatch.netwatch.device.response.DeviceResponse;
import com.NetWatch.netwatch.device.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @GetMapping()
    public List<DeviceResponse> list() {
        return service.findAll().stream().map(DeviceResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DeviceResponse get(@PathVariable Long id) {
        return DeviceResponse.from(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequest req) {
        Device created = service.create(req);
        return ResponseEntity.created(URI.create("/api/devices/" + created.getId()))
                .body(DeviceResponse.from(created));
    }

    @PutMapping("/{id}")
    public DeviceResponse update(@PathVariable Long id,
                                 @Valid @RequestBody DeviceRequest req) {
        return DeviceResponse.from(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
