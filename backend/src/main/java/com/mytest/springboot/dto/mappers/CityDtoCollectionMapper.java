package com.mytest.springboot.dto.mappers;

import com.mytest.springboot.dto.CityDto;
import com.mytest.springboot.models.City;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface CityDtoCollectionMapper {

    List<CityDto> toCityDtoCollection(List<City> entities);

    List<City> toCityEntityCollection(List<CityDto> dtos);

}
