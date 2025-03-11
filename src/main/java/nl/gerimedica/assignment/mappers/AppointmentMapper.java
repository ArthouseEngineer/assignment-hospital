package nl.gerimedica.assignment.mappers;

import nl.gerimedica.assignment.dto.AppointmentDTO;
import nl.gerimedica.assignment.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PatientMapper.class})
public interface AppointmentMapper {


    @Mapping(source = "patient", target = "patient")
    AppointmentDTO toDto(Appointment appointment);


    Appointment toEntity(AppointmentDTO dto);

}
