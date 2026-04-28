package com.makurohashami.realtorconnect.controller;

import com.makurohashami.realtorconnect.annotation.security.IsSameUser;
import com.makurohashami.realtorconnect.dto.apiresponse.ApiSuccess;
import com.makurohashami.realtorconnect.dto.realtor.RealtorAddDto;
import com.makurohashami.realtorconnect.dto.realtor.RealtorDto;
import com.makurohashami.realtorconnect.dto.realtor.RealtorFilter;
import com.makurohashami.realtorconnect.dto.realtor.RealtorFullDto;
import com.makurohashami.realtorconnect.service.realtor.RealtorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.makurohashami.realtorconnect.util.ApiResponseUtil.ok;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/realtors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Realtor Controller", description = "Allows you manage your information(if you are realtor), and see other's realtors")
public class RealtorController {

    private final RealtorService service;

    @IsSameUser
    @GetMapping("/{id}/full")
    @Operation(summary = "Get full realtor")
    public ResponseEntity<ApiSuccess<RealtorFullDto>> readFullById(@PathVariable long id) {
        return ok(service.readFullById(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get short realtor")
    public ResponseEntity<ApiSuccess<RealtorDto>> readShortById(@PathVariable long id) {
        return ok(service.readShortById(id));
    }

    @GetMapping
    @Operation(summary = "Get page of short realtors")
    public ResponseEntity<ApiSuccess<Page<RealtorDto>>> getAllShorts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @ModelAttribute RealtorFilter filter
    ) {
        Pageable paging = PageRequest.of(page, size);
        return ok(service.getAllShorts(filter, paging));
    }

    @IsSameUser
    @PutMapping("/{id}")
    @Operation(summary = "Update realtor")
    public ResponseEntity<ApiSuccess<RealtorFullDto>> update(@PathVariable long id,
                                                             @RequestBody @Valid RealtorAddDto dto) {
        return ok(service.update(id, dto));
    }

    @IsSameUser
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete realtor")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
