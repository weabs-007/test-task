package com.mytest.springboot.services;

import com.mytest.springboot.exceptions.CityNotFoundException;
import com.mytest.springboot.models.City;

import java.util.List;

public interface CityService {

    List<City> getAllCities();
    City updateCity(City city) throws CityNotFoundException;
//    City updateCity(City city);
    City getCityById(long id) throws CityNotFoundException;

}
