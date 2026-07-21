package com.aipe.evidence.api.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Evidence 验证请求 DTO
 *
 * <p>对齐 WP014 Blueprint §7 POST /api/v1/evidences/{id}/verify
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class VerifyEvidenceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否通过（true = VERIFIED；false = REJECTED） */
    private boolean approved;

    /** 验证人/系统 */
    private String verifier;

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    public String getVerifier() { return verifier; }
    public void setVerifier(String verifier) { this.verifier = verifier; }
}
