package com.mytest.springboot.repository;

import com.mytest.springboot.models.City;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@NoArgsConstructor
public class CityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CityRepository repo;

    @Test
    public void should_find_no_cities_if_repository_is_empty() {
        Iterable<City> cities = repo.findAll();

        assertThat(cities).isEmpty();
    }

    @Test
    public void should_find_all_cities() {
        City city1 = repo.save(new City(1L, "name1", "http://name1.jpg"));
        entityManager.persist(city1);
        City city2 = repo.save(new City(2L, "name2", "http://name2.jpg"));
        entityManager.persist(city2);

        List<City> cities = repo.findAll();

        cities.stream().forEach(System.out::println);

        assertThat(cities.size()).isGreaterThan(0);
        assertThat(cities).hasSize(2).contains(city1, city2);
    }

    @Test
    public void should_store_a_city() {
        City tutorial = repo.save(new City(3L, "name3", "http://name3.jpg"));

        assertThat(tutorial).hasFieldOrPropertyWithValue("id", 3L);
        assertThat(tutorial).hasFieldOrPropertyWithValue("name", "name3");
        assertThat(tutorial).hasFieldOrPropertyWithValue("photo", "http://name3.jpg");
    }

    @Test
    public void should_find_city_by_id() {
        City city1 = repo.save(new City(4L, "name4", "http://name4.jpg"));
        entityManager.persist(city1);
        City city2 = repo.save(new City(5L, "name5", "http://name5.jpg"));
        entityManager.persist(city2);

        City foundCity = repo.findById(city2.getId()).get();

        assertThat(foundCity).isEqualTo(city2);
    }
}