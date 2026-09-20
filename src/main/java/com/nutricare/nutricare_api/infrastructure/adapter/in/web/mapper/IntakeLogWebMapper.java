package com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper;

import com.nutricare.nutricare_api.core.application.dto.CreateIntakeEntryCommand;
import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogHeaderResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateIntakeEntryCommand;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateIntakeEntryRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.IntakeEntryResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.IntakeLogHeaderResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateIntakeEntryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IntakeLogWebMapper {
    IntakeLogHeaderResponse toHeaderResponse(IntakeLogHeaderResult result);

    List<IntakeLogHeaderResponse> toHeaderResponseList(List<IntakeLogHeaderResult> results);

    IntakeEntryResponse toEntryResponse(IntakeEntryResult result);

    List<IntakeEntryResponse> toEntryResponseList(List<IntakeEntryResult> results);

    @Mapping(target = "intakeLogId", source = "intakeLogId")
    CreateIntakeEntryCommand toCreateCommand(Integer intakeLogId, CreateIntakeEntryRequest request);

    @Mapping(target = "intakeLogId", source = "intakeLogId")
    @Mapping(target = "entryId", source = "entryId")
    UpdateIntakeEntryCommand toUpdateCommand(Integer intakeLogId, Integer entryId, UpdateIntakeEntryRequest request);
}
