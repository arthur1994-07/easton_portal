package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.data.NotificationInfo;
import com.common.easton_portal.misc.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired private Notification mNotification;

    @PostMapping("/get-data")
    @Operation(summary = "Provide the notification list for user", description = "Provide the notification list for user")
    public ResponseEntity<JsonRespond<List<NotificationInfo.Base>>> getNotification() throws Throwable {
        var notification = mNotification.getData();
        return ResponseEntity.ok(new JsonRespond<>(notification));
    }
}
