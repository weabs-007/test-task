package com.mytest.springboot.dto.mappers;

import com.mytest.springboot.dto.CityDto;
import com.mytest.springboot.models.City;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
@Component
public interface CityDtoMapper {

    CityDto toCityDto(City entity);

    City toCityEntity(CityDto dto);

}
