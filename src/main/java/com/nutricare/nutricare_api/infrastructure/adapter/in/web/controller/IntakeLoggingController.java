package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.port.in.LogIntakeUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateIntakeEntryRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.IntakeEntryResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.IntakeLogHeaderResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateIntakeEntryRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.IntakeLogWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/intake-logs")
@RequiredArgsConstructor
public class IntakeLoggingController {
    private final LogIntakeUseCase useCase;
    private final IntakeLogWebMapper mapper;

    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<ApiResponse<List<IntakeLogHeaderResponse>>> listByProfile (@PathVariable Integer profileId,
                                                                                    @RequestParam Integer pageIndex,
                                                                                    @RequestParam Integer pageSize) {
        return ApiResponse.ok(mapper.toHeaderResponseList(useCase.listLogs(profileId, pageIndex, pageSize)));
    }

    @PostMapping("/profiles/{profileId}")
    public ResponseEntity<ApiResponse<IntakeLogHeaderResponse>> createLog(@PathVariable Integer profileId,
                                                                           @RequestParam LocalDate date) {
        var createdLog = useCase.createLog(profileId, date);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/intake-logs/{id}")
                .buildAndExpand(createdLog.id())
                .toUri();

        return ApiResponse.created(location, mapper.toHeaderResponse(createdLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Integer id) {
        useCase.deleteLogById(id);
        return ApiResponse.noContent();
    }

    @GetMapping("/profiles/{profileId}/{date}")
    public ResponseEntity<ApiResponse<List<IntakeEntryResponse>>> listEntries (@PathVariable Integer profileId,
                                                                               @PathVariable LocalDate date) {
        return ApiResponse.ok(mapper.toEntryResponseList(useCase.listEntriesOnDate(profileId, date)));
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<ApiResponse<IntakeEntryResponse>> getEntry(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.toEntryResponse(useCase.findEntryById(id).orElseThrow()));
    }

    @PostMapping("/{intakeLogId}/entries")
    public ResponseEntity<ApiResponse<IntakeEntryResponse>> createEntry(@PathVariable Integer intakeLogId, @RequestBody CreateIntakeEntryRequest req) {
        var createdEntry = useCase.createEntry(mapper.toCreateCommand(intakeLogId, req));
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/intake-logs/entries/{id}")
                .buildAndExpand(createdEntry.id())
                .toUri();

        return ApiResponse.created(location, mapper.toEntryResponse(createdEntry));
    }

    @PutMapping("/{intakeLogId}/entries/{entryId}")
    public ResponseEntity<ApiResponse<IntakeEntryResponse>> updateEntry(@PathVariable Integer intakeLogId,
                                                                         @PathVariable Integer entryId,
                                                                         @RequestBody UpdateIntakeEntryRequest req) {
        return ApiResponse.ok(mapper.toEntryResponse(useCase.updateEntry(mapper.toUpdateCommand(intakeLogId, entryId, req))));
    }

    @DeleteMapping("/{intakeLogId}/entries/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Integer intakeLogId, @PathVariable Integer entryId) {
        useCase.deleteEntryById(intakeLogId, entryId);
        return ApiResponse.noContent();
    }
}