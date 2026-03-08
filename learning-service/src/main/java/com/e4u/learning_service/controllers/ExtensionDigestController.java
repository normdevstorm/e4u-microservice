package com.e4u.learning_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e4u.learning_service.dtos.request.ExtensionWordCreateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.services.ExtensionDigestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST Controller for handling browser extension word saves.
 * Saves words to global dictionary and creates context templates for batch
 * processing.
 */
@RestController
@RequestMapping("/v1/extension-digests")
@RequiredArgsConstructor
@Tag(name = "Extension Digest", description = "APIs for browser extension to save words and contexts")
public class ExtensionDigestController {

    private final ExtensionDigestService extensionDigestService;

    @PostMapping("/save")
    @Operation(summary = "Save word from browser extension", description = "Saves a word to global dictionary if lemma + type doesn't exist, "
            +
            "and creates a word context template for later batch processing")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Word and context saved successfully"),
            @ApiResponse(responseCode = "200", description = "Word already exists, context saved"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<BaseResponse<Void>> saveWordExtension(
            @Valid @RequestBody ExtensionWordCreateRequest request) {
        boolean isNewWord = extensionDigestService.saveWordFromExtension(request);

        String message = isNewWord
                ? "Word and context saved successfully"
                : "Word already exists, context saved for batch processing";

        HttpStatus status = isNewWord ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.status(status).body(BaseResponse.ok(message));
    }

}