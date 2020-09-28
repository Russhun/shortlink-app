package com.artemiysaltsin.shortlinks.controller;

import com.artemiysaltsin.shortlinks.form.CustomLinkForm;
import com.artemiysaltsin.shortlinks.form.LinkForm;
import com.artemiysaltsin.shortlinks.model.LinkModel;
import com.artemiysaltsin.shortlinks.repository.LinksRepository;
import com.artemiysaltsin.shortlinks.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;


@RestController
public class ShortLinkController {

    @Autowired
    LinksRepository linksRepository;

    @Value("${serverId}")
    int serverId;

    @Value("7")
    int expireAfter;

    @Value("${serverUrl}")
    String serverUrl;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/api/post/longUrl", method = RequestMethod.POST)
    public ObjectNode postShortLink(@RequestBody LinkForm linkForm) {

        ObjectNode objectNode = mapper.createObjectNode();

        if (!Utils.isCorrectURL(linkForm.getUrl()))
            return objectNode.put("status", HttpStatus.BAD_REQUEST.toString());


        // Приводим время к Московскому
        // И запоминаем unix time(до мс.) как BigInteger
        // Для более простого перевода в 36ричную систему счисления
        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow"));
        BigInteger dateAsTime = new BigInteger(Long.toString(zonedDateTime.toInstant().toEpochMilli()));

        zonedDateTime.plusDays(expireAfter);

        // toString(36) переведёт в 36ричную систему
        String shortPath = dateAsTime.toString(36)+serverId;
        LinkModel linkModel = linksRepository.findByShortPath(shortPath);
        if (linkModel != null)
            return objectNode.put("status", HttpStatus.CONFLICT.toString());

        String apiUrl = serverUrl+"api/redirect/"+shortPath;
        String shortUrl = serverUrl+shortPath;

        linksRepository.save(new LinkModel(shortPath, linkForm.getUrl(), zonedDateTime));

        return objectNode.put("status", HttpStatus.OK.toString())
                            .put("expireAfter", String.format("%s days", expireAfter))
                            .put("shortUrl", shortUrl)
                            .put("apiUrl", apiUrl)
                            .put("longUrl", linkForm.getUrl());
    }


    @RequestMapping(value = "/api/post/customShortPath", method = RequestMethod.POST)
    public ObjectNode postCustomShortLink(@RequestBody CustomLinkForm customLinkForm) {

        ObjectNode objectNode = mapper.createObjectNode();


        if (customLinkForm.getUrl() == null || customLinkForm.getShortUserPath() == null)
            return objectNode.put("status", HttpStatus.BAD_REQUEST.toString());

        if (!Utils.isCorrectURL(customLinkForm.getUrl()))
            return objectNode.put("status", HttpStatus.BAD_REQUEST.toString());


        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow"));
        zonedDateTime.plusDays(expireAfter);

        String shortPath = customLinkForm.getShortUserPath();

        LinkModel linkModel = linksRepository.findByShortPath(shortPath);
        if (linkModel != null)
            return objectNode.put("status", HttpStatus.CONFLICT.toString());



        String apiUrl = serverUrl+"api/redirect/"+shortPath;
        String shortUrl = serverUrl+shortPath;

        // Проверка валидности ссылки и shortPath
        // Если кастомный shortPath будет содержать что-то кроме букв, цифр, тире и нижнего подчеркивания
        // то возвращаем BAD_REQUEST
        if (Pattern.compile("[\\W&&[^-]]").matcher(shortPath).find()
            || !Utils.isCorrectURL(apiUrl) || !Utils.isCorrectURL(shortUrl)) {
            return objectNode.put("status", HttpStatus.BAD_REQUEST.toString());
        }

        linksRepository.save(new LinkModel(shortPath, customLinkForm.getUrl(), zonedDateTime));

        return objectNode.put("status", HttpStatus.OK.toString())
                .put("expireAfter", String.format("%s days", expireAfter))
                .put("shortUrl", shortUrl)
                .put("apiShortUrl", apiUrl)
                .put("longUrl", customLinkForm.getUrl());
    }


    @RequestMapping(value = "/api/redirect/{shortPath}", method = RequestMethod.GET)
    public ObjectNode redirectToLinkApi(@PathVariable String shortPath) {

        ObjectNode objectNode = mapper.createObjectNode();
        LinkModel linkModel = linksRepository.findByShortPath(shortPath);
        if (linkModel == null) return objectNode.put("status", HttpStatus.NOT_FOUND.toString());
        else {
            return objectNode.put("status", HttpStatus.PERMANENT_REDIRECT.toString())
                    .put("redirectTo", linkModel.getUrl());
        }

    }

    @RequestMapping(value = "/{shortPath}", method = RequestMethod.GET)
    public ObjectNode redirectToLink(HttpServletResponse response, @PathVariable String shortPath) throws IOException {

        ObjectNode objectNode = mapper.createObjectNode();

        LinkModel linkModel = linksRepository.findByShortPath(shortPath);
        if (linkModel == null) return objectNode.put("status", HttpStatus.NOT_FOUND.toString());
        else {
            response.sendRedirect(linkModel.getUrl());
            return objectNode.put("status", HttpStatus.PERMANENT_REDIRECT.toString())
                    .put("redirectTo", linkModel.getUrl());
        }


    }



}
