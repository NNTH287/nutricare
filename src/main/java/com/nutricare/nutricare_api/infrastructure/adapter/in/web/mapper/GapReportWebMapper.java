package com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper;

import com.nutricare.nutricare_api.core.application.dto.GapReportResult;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.GapReportResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GapReportWebMapper {
    GapReportResponse toResponse(GapReportResult result);
}
