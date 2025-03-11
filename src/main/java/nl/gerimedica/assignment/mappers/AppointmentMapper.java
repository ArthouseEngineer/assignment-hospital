package nl.gerimedica.assignment.mappers;

import nl.gerimedica.assignment.dto.AppointmentDTO;
import nl.gerimedica.assignment.entity.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PatientMapper.class})
public interface AppointmentMapper {


    AppointmentDTO toDto(Appointment appointment);


    Appointment toEntity(AppointmentDTO dto);

}
