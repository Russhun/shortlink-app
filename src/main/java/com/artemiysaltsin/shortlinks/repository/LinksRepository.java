package com.artemiysaltsin.shortlinks.repository;


import com.artemiysaltsin.shortlinks.model.LinkModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinksRepository extends JpaRepository<LinkModel, String> {

    LinkModel findByShortPath(String shortPath);

}
