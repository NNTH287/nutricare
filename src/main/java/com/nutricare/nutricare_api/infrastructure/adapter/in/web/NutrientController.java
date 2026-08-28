package com.nutricare.nutricare_api.infrastructure.adapter.in.web;

import com.nutricare.nutricare_api.core.application.dto.NutrientResult;
import com.nutricare.nutricare_api.core.application.service.ManageNutrientService;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateNutrientRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.NutrientResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateNutrientRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.NutrientWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/nutrients")
@RequiredArgsConstructor
public class NutrientController {
    private final ManageNutrientService manageNutrientService;
    private final NutrientWebMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NutrientResponse>> getNutrient(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.toResponse(manageNutrientService.findById(id).orElseThrow()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NutrientResponse>> createNutrient(@RequestBody CreateNutrientRequest req) {
        NutrientResult savedNutrient = manageNutrientService.save(mapper.toCreateCommand(req));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedNutrient.id())
                .toUri();

        return ApiResponse.created(location, mapper.toResponse(savedNutrient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NutrientResponse>> updateNutrient(@PathVariable Integer id, @RequestBody UpdateNutrientRequest req) {
        return ApiResponse.ok(mapper.toResponse(manageNutrientService.update(mapper.toUpdateCommand(id, req))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNutrient(@PathVariable Integer id) {
        manageNutrientService.deleteById(id);
        return ApiResponse.noContent();
    }
}
