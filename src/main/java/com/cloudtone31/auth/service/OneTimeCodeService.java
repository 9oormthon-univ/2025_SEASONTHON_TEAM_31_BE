package com.cloudtone31.auth.service;

import java.time.Duration;

public interface OneTimeCodeService {
    String issue(String userId, Duration ttl);
    String consume(String code);
}