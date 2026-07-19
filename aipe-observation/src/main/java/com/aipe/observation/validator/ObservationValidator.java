package com.aipe.observation.validator;

import com.aipe.observation.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationValidator {
    private static final Logger log = LoggerFactory.getLogger(ObservationValidator.class);

    public ValidationResult validate(Observation observation) {
        if (observation == null) {
            return ValidationResult.fail("Observation is null");
        }
        if (observation.getId() == null || observation.getId().isEmpty()) {
            return ValidationResult.fail("Observation ID is empty");
        }
        if (observation.getResource() == null) {
            return ValidationResult.fail("Resource reference is missing");
        }
        if (observation.getResource().getResourceType() == null) {
            return ValidationResult.fail("Resource type is missing");
        }
        if (observation.getMetrics() == null || observation.getMetrics().isEmpty()) {
            return ValidationResult.fail("Metrics list is empty");
        }
        if (observation.getEventTime() <= 0) {
            return ValidationResult.fail("Invalid event time");
        }
        return ValidationResult.success();
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String reason;

        private ValidationResult(boolean valid, String reason) {
            this.valid = valid;
            this.reason = reason;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult fail(String reason) {
            return new ValidationResult(false, reason);
        }

        public boolean isValid() { return valid; }
        public String getReason() { return reason; }
    }
}
