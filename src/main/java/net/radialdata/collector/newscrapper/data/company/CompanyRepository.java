package net.radialdata.collector.newscrapper.data.company;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by morbo on 11/12/17.
 */
public interface CompanyRepository extends CrudRepository<Company, Integer> {

    Company findBySymbol(String symbol);
    Company findByName(String name);
    List<Company> findBySector(String sector);
    List<Company> findByIndustry(String industry);
}
