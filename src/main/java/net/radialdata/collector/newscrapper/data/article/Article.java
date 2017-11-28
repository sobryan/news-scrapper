package net.radialdata.collector.newscrapper.data.article;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Article {

    public Article(String symbol, String url, Timestamp publishDate) {
        this.url = url;
        this.publishDate = publishDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    String symbol;
    String url;
    Timestamp publishDate;
    String content;
    Float score;
    Float magnitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Float magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", url='" + url + '\'' +
                ", publishDate=" + publishDate +
                ", content='" + content + '\'' +
                ", score=" + score +
                ", magnitude=" + magnitude +
                '}';
    }
}
