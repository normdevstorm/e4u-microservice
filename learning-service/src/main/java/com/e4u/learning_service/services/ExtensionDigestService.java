package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.ExtensionWordCreateRequest;

/**
 * Service interface for browser extension digest operations.
 * Handles saving words from browser extension to global dictionary
 * and creating word context templates for batch processing.
 */
public interface ExtensionDigestService {

    /**
     * Save a word from the browser extension.
     * - If the word (lemma + type) doesn't exist in global dictionary, creates it.
     * - Creates a word context template for later batch processing.
     *
     * @param request The word data from the extension
     * @return true if a new word was created, false if word already existed
     */
    boolean saveWordFromExtension(ExtensionWordCreateRequest request);
}
