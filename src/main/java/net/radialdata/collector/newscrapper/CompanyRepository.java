package net.radialdata.collector.newscrapper;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by morbo on 11/12/17.
 */
public interface CompanyRepository extends CrudRepository<Company, String> {

    Company findBySymbol(String symbol);
    Company findByName(String name);
    List<Company> findBySector(String sector);
    List<Company> findByIndustry(String industry);
}
