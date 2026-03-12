package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.response.OnboardingOptionResponse;
import com.e4u.learning_service.repositories.OnboardingInterestRepository;
import com.e4u.learning_service.repositories.OnboardingOccupationRepository;
import com.e4u.learning_service.services.OnboardingReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingReferenceServiceImpl implements OnboardingReferenceService {

    private final OnboardingOccupationRepository occupationRepository;
    private final OnboardingInterestRepository interestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OnboardingOptionResponse> getOccupations() {
        return occupationRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(o -> OnboardingOptionResponse.builder()
                        .id(o.getId().toString())
                        .code(o.getCode())
                        .label(o.getLabel())
                        .sortOrder(o.getSortOrder())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OnboardingOptionResponse> getInterests() {
        return interestRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(i -> OnboardingOptionResponse.builder()
                        .id(i.getId().toString())
                        .code(i.getCode())
                        .label(i.getLabel())
                        .sortOrder(i.getSortOrder())
                        .build())
                .toList();
    }

    @Override
    public List<OnboardingOptionResponse> getCommitmentTimeOptions() {
        // Fixed presets — no DB table needed; values are minutes/day.
        // If this list needs to change, update here and redeploy.
        return List.of(
                option("5", "5", "5 minutes / day", 0),
                option("10", "10", "10 minutes / day", 1),
                option("15", "15", "15 minutes / day", 2),
                option("20", "20", "20 minutes / day", 3),
                option("30", "30", "30 minutes / day", 4),
                option("45", "45", "45 minutes / day", 5),
                option("60", "60", "1 hour / day", 6));
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private OnboardingOptionResponse option(String id, String code, String label, int sortOrder) {
        return OnboardingOptionResponse.builder()
                .id(id)
                .code(code)
                .label(label)
                .sortOrder(sortOrder)
                .build();
    }
}
