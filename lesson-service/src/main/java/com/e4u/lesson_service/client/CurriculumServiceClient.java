package com.e4u.lesson_service.client;

import com.e4u.lesson_service.client.dto.CurriculumServiceResponse;
import com.e4u.lesson_service.client.dto.CurriculumUnitDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for communicating with the Curriculum Service.
 * 
 * <p>
 * This client provides methods to interact with the curriculum-service
 * microservice for retrieving curriculum and unit information.
 * Uses Eureka service discovery to locate the curriculum-service instance.
 * </p>
 */
@FeignClient(name = "curriculum-service", fallback = CurriculumServiceClientFallback.class)
public interface CurriculumServiceClient {

        /**
         * Get all units for a specific curriculum.
         * 
         * @param curriculumId The UUID of the curriculum
         * @return List of curriculum units
         */
        @GetMapping("/v1/curriculum-units/curriculum/{curriculumId}")
        CurriculumServiceResponse<List<CurriculumUnitDTO>> getUnitsByCurriculumId(
                        @PathVariable("curriculumId") UUID curriculumId);

        /**
         * Get a specific curriculum unit by ID.
         * 
         * @param unitId The UUID of the unit
         * @return The curriculum unit
         */
        @GetMapping("/v1/curriculum-units/{unitId}")
        CurriculumServiceResponse<CurriculumUnitDTO> getUnitById(
                        @PathVariable("unitId") UUID unitId);
}
