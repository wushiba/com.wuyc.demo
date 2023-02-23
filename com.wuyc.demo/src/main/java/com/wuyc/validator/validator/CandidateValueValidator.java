package com.wuyc.validator.validator;

import com.wuyc.validator.annotation.CandidateValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CandidateValueValidator implements ConstraintValidator<CandidateValue, Object> {

    private final Set<String> candidates = new HashSet<>();

    @Override
    public void initialize(CandidateValue annotation) {
        candidates.addAll(Arrays.asList(annotation.candidateValue()));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return candidates.contains(value.toString());
    }
}
