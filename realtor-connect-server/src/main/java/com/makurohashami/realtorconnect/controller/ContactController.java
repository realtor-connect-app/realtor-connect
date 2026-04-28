package com.makurohashami.realtorconnect.controller;

import com.makurohashami.realtorconnect.annotation.security.IsContactOwner;
import com.makurohashami.realtorconnect.annotation.security.IsSameRealtor;
import com.makurohashami.realtorconnect.dto.apiresponse.ApiSuccess;
import com.makurohashami.realtorconnect.dto.realtor.ContactDto;
import com.makurohashami.realtorconnect.service.contact.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.makurohashami.realtorconnect.util.ApiResponseUtil.created;
import static com.makurohashami.realtorconnect.util.ApiResponseUtil.ok;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/realtors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Contact Controller", description = "Allows you manage your own contacts, and see other's contacts")
public class ContactController {

    private final ContactService service;

    @IsSameRealtor
    @PostMapping("/{realtorId}/contacts")
    @Operation(summary = "Add new contact")
    public ResponseEntity<ApiSuccess<ContactDto>> create(@PathVariable long realtorId,
                                                         @RequestBody ContactDto contactDto) {
        return created(service.create(realtorId, contactDto));
    }

    @GetMapping("/contacts/{contactId}")
    @Operation(summary = "Get contact")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccess<ContactDto>> readById(@PathVariable long contactId) {
        return ok(service.readById(contactId));
    }

    @GetMapping("/{realtorId}/contacts")
    @Operation(summary = "Get all contacts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiSuccess<List<ContactDto>>> readAll(@PathVariable long realtorId) {
        return ok(service.readAll(realtorId));
    }

    @IsContactOwner
    @PutMapping("/contacts/{contactId}")
    @Operation(summary = "Update contact")
    public ResponseEntity<ApiSuccess<ContactDto>> update(@PathVariable long contactId,
                                                         @RequestBody ContactDto contactDto) {
        return ok(service.update(contactId, contactDto));
    }

    @IsContactOwner
    @DeleteMapping("/contacts/{contactId}")
    @Operation(summary = "Delete contact")
    public ResponseEntity<Void> delete(@PathVariable long contactId) {
        service.delete(contactId);
        return ResponseEntity.noContent().build();
    }

}
