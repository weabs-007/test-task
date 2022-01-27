package com.mytest.springboot.services;

import com.mytest.springboot.exceptions.CityNotFoundException;
import com.mytest.springboot.models.City;
import com.mytest.springboot.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City updateCity(City city) throws CityNotFoundException {
        Optional<City> cityData = cityRepository.findById(city.getId());
        if (cityData.isPresent()) {
            return cityRepository.save(city);
        } else {
            throw new CityNotFoundException("City not found with id : " + city.getId());
        }
    }

    @Override
    public City getCityById(long id) throws CityNotFoundException {
        return cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException("No city found with Id = " + id));
    }

}


