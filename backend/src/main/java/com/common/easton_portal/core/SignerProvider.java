package com.common.easton_portal.core;

import com.common.core.web.security.base.CertificateSigner;

import java.time.Duration;

public interface SignerProvider {
    CertificateSigner getWriteSigner();
    CertificateSigner[] getReadSigners();
    Duration getExpirationPeriod();
}
