package com.artemiysaltsin.shortlinks.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "link")
public class LinkModel {

    @Id
    @Column(name = "short_path")
    String shortPath;

    @Column
    String url;

    @Column(name = "expire")
    ZonedDateTime expireDateTime;

}
