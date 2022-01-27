package com.mytest.springboot.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytest.springboot.models.City;
import com.mytest.springboot.services.CityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppControllerTest {

    @Mock
    private CityService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppController appController;

    @Autowired
    private ObjectMapper objectMapper;


    private static final String URL = "/api/v1";

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(appController).build();
    }

    @Test
    @WithMockUser(username = "editor", roles = {"ALLOW_EDIT"})
    public void getCityByIdSuccess() throws Exception {
        mockMvc.perform(get(URL + "/cities/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(20));
    }

    @Test
    @WithMockUser(username = "editor", roles = {"ALLOW_EDIT"})
    public void getCityByIdFailed() throws Exception {
        mockMvc.perform(get(URL + "/cities/1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user")
    public void getAllCities() throws Exception {
        mockMvc.perform(get(URL + "/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "editor", roles = {"ALLOW_EDIT"})
    public void updateCity() throws Exception {
        City updateCity = new City();
        updateCity.setId(5L);
        updateCity.setName("update_Name");
        updateCity.setPhoto("update_Photo");
        mockMvc.perform(put(URL + "/cities/{id}" , updateCity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(convertToJson(updateCity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(updateCity.getId()))
                .andExpect(jsonPath("name").value(updateCity.getName()))
                .andExpect(jsonPath("photo").value(updateCity.getPhoto()));
    }

    private String convertToJson(City city) throws JsonProcessingException {
        return objectMapper.writeValueAsString(city);
    }

}
