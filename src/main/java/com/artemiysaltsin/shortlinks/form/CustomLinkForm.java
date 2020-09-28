package com.artemiysaltsin.shortlinks.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CustomLinkForm {

    String url;

    String shortUserPath;

}
