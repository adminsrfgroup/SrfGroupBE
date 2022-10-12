package com.takirahal.srfgroup.modules.rentrequest.mapper;

import com.takirahal.srfgroup.mapper.EntityMapper;
import com.takirahal.srfgroup.modules.rentrequest.dto.RentRequestDTO;
import com.takirahal.srfgroup.modules.rentrequest.entities.RentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface RentRequestMapper  extends EntityMapper<RentRequestDTO, RentRequest> {
}
