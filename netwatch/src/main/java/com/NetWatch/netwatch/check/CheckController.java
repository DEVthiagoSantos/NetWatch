package com.NetWatch.netwatch.check;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class CheckController {

    private final MonitoringService monitoring;

    public CheckController(MonitoringService monitoring) {
        this.monitoring = monitoring;
    }

    @PostMapping("/{id}/check")
    public CheckResultResponse checkNow(@PathVariable Long id) {
        return CheckResultResponse.from(monitoring.checkNow(id));
    }

    @PostMapping("/check-all")
    public List<CheckResultResponse> checkAll() {
        return monitoring.checkAllActive().stream().map(CheckResultResponse::from).toList();
    }

    @GetMapping("/{id}/checks")
    public List<CheckResultResponse> history(@PathVariable Long id,
                                             @RequestParam(defaultValue = "50") int size) {
        return monitoring.history(id, size).stream().map(CheckResultResponse::from).toList();
    }
}