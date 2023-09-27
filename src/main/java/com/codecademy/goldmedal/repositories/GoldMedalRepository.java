package com.codecademy.goldmedal.repositories;

import com.codecademy.goldmedal.model.GoldMedal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoldMedalRepository extends CrudRepository<GoldMedal, Long> {
    List<GoldMedal> findByCountry(String country);
    List<GoldMedal> findByCountryOrderByYearAsc(String country);
    List<GoldMedal> findByCountryOrderByYearDesc(String country);
    List<GoldMedal> findByCountryOrderBySeasonAsc(String country);
    List<GoldMedal> findByCountryOrderBySeasonDesc(String country);
    List<GoldMedal> findByCountryOrderByCityAsc(String country);
    List<GoldMedal> findByCountryOrderByCityDesc(String country);
    List<GoldMedal> findByCountryOrderByNameAsc(String country);
    List<GoldMedal> findByCountryOrderByNameDesc(String country);
    List<GoldMedal> findByCountryOrderByEventAsc(String country);
    List<GoldMedal> findByCountryOrderByEventDesc(String country);
    List<GoldMedal> findByCountryAndSeasonOrderByYearAsc(String country, String season);
    List <GoldMedal> findBySeason(String season);
    List <GoldMedal> findByCountryAndGender(String country, String gender);
}

