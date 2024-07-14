package com.inv.walletCare.rest.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling service status checks.
 * This controller provides endpoints for checking the operational status of the service.
 */
@RequestMapping("/status")
@RestController
public class StatusRestController {

    /**
     * Endpoint to check the operational status of the service.
     * @return A simple string indicating that the service is running.
     */
    @GetMapping
    public String getStatus() {
        return "Service is running";
    }
}