package net.radialdata.collector.newscrapper.data.article;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, Integer> {

    List<Article> findByScoreIsNotNull();

}
