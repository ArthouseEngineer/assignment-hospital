package nl.gerimedica.assignment.mappers;

import nl.gerimedica.assignment.dto.PatientDTO;
import nl.gerimedica.assignment.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {


    PatientDTO toDto(Patient patient);


    @Mapping(target = "appointments", ignore = true)
    Patient toEntity(PatientDTO dto);

}
