package com.NetWatch.netwatch.check;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    Page<CheckResult> findByDeviceIdOrderByCheckedAtDesc(Long deviceId, Pageable pageable);
}
