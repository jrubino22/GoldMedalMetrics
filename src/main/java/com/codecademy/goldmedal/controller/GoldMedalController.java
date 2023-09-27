package com.codecademy.goldmedal.controller;

import com.codecademy.goldmedal.repositories.CountryRepository;
import com.codecademy.goldmedal.repositories.GoldMedalRepository;
import com.codecademy.goldmedal.model.*;
import org.apache.commons.text.WordUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {
    // TODO: declare references to your com.codecademy.goldmedal.repositories
    private GoldMedalRepository goldMedalRepository;
    private CountryRepository countryRepository;
    // TODO: update your constructor to include your com.codecademy.goldmedal.repositories
    public GoldMedalController(GoldMedalRepository goldMedalRepository, CountryRepository countryRepository) {
        this.goldMedalRepository = goldMedalRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam String sort_by, @RequestParam String ascending) {
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable String country) {
        String countryName = WordUtils.capitalizeFully(country);
        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country, @RequestParam String sort_by, @RequestParam String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy, boolean ascendingOrder) {
        List<GoldMedal> medalsList;
        switch (sortBy) {
            case "year":
                medalsList = ascendingOrder
                        ? this.goldMedalRepository.findByCountryOrderByYearAsc(countryName)
                        : this.goldMedalRepository.findByCountryOrderByYearDesc(countryName);
                break;
            case "season":
                medalsList = ascendingOrder
                        ? this.goldMedalRepository.findByCountryOrderBySeasonAsc(countryName)
                        : this.goldMedalRepository.findByCountryOrderBySeasonDesc(countryName);
                break;
            case "city":
                medalsList = ascendingOrder
                        ? this.goldMedalRepository.findByCountryOrderByCityAsc(countryName)
                        : this.goldMedalRepository.findByCountryOrderByCityDesc(countryName);
                break;
            case "name":
                medalsList = ascendingOrder
                        ? this.goldMedalRepository.findByCountryOrderByNameAsc(countryName)
                        : this.goldMedalRepository.findByCountryOrderByNameDesc(countryName);
                break;
            case "event":
                medalsList = ascendingOrder
                        ? this.goldMedalRepository.findByCountryOrderByEventAsc(countryName)
                        : this.goldMedalRepository.findByCountryOrderByEventDesc(countryName);
                break;
            default:
                medalsList = new ArrayList<>();
                break;
        }

        return new CountryMedalsListResponse(medalsList);
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        Optional<Country> countryOptional = this.countryRepository.getByName(countryName);
        if (countryOptional.isEmpty()) {
            return new CountryDetailsResponse(countryName);
        }

        Country country = countryOptional.get();

        List <GoldMedal> medalList = this.goldMedalRepository.findByCountry(countryName);

        Integer goldMedalCount = medalList.size();

        var summerWins = this.goldMedalRepository.findByCountryAndSeasonOrderByYearAsc(countryName, "summer");
        var numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;
        List <GoldMedal> summerEventList = this.goldMedalRepository.findBySeason("summer");
        int totalSummerEvents = summerEventList.size();
        var percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != null ? (float) summerWins.size() / totalSummerEvents : null;
        var yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        var winterWins = this.goldMedalRepository.findByCountryAndSeasonOrderByYearAsc(countryName, "winter");
        var numberWinterWins = winterWins.size() > 0 ? winterWins.size() : null;
        List <GoldMedal> winterEventList = this.goldMedalRepository.findBySeason("winter");
        var totalWinterEvents = winterEventList.size() > 0 ? winterEventList.size() : null;
        var percentageTotalWinterWins = totalWinterEvents != 0 && numberWinterWins != null ? (float) winterWins.size() / totalWinterEvents : null;
        var yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;
        List <GoldMedal> byCountryAndGenderFemale = this.goldMedalRepository.findByCountryAndGender(countryName, "female");
        var numberEventsWonByFemaleAthletes = byCountryAndGenderFemale.size();
        List <GoldMedal> byCountryAndGenderMale = this.goldMedalRepository.findByCountryAndGender(countryName, "male");
        var numberEventsWonByMaleAthletes = byCountryAndGenderMale.size();

        return new CountryDetailsResponse(
                countryName,
                country.getGdp(),
                country.getPopulation(),
                goldMedalCount,
                numberSummerWins,
                percentageTotalSummerWins,
                yearFirstSummerWin,
                numberWinterWins,
                percentageTotalWinterWins,
                yearFirstWinterWin,
                numberEventsWonByFemaleAthletes,
                numberEventsWonByMaleAthletes);
    }

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;
        switch (sortBy) {
            case "name":
                countries = ascendingOrder
                        ? this.countryRepository.getAllByOrderByNameAsc()
                        : this.countryRepository.getAllByOrderByNameDesc();
                break;
            case "gdp":
                countries = ascendingOrder
                        ? this.countryRepository.getAllByOrderByGdpAsc()
                        : this.countryRepository.getAllByOrderByGdpDesc();
                break;
            case "population":
                countries = ascendingOrder
                        ? this.countryRepository.getAllByOrderByPopulationAsc()
                        : this.countryRepository.getAllByOrderByPopulationDesc();
                break;
            case "medals":
            default:
                countries = this.countryRepository.getAllByOrderByNameAsc();
                break;
        }

        var countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries.stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();
        for (var country : countries) {
            String countryName = country.getName();
            List <GoldMedal> allMedals = this.goldMedalRepository.findByCountry(countryName);
            var goldMedalCount = allMedals.size();
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }
        return countrySummaries;
    }
}
