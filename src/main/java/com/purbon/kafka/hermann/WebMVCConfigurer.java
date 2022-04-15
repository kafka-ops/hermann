package com.purbon.kafka.hermann;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Configuration
public class WebMVCConfigurer implements WebMvcConfigurer {

    public static final MediaType MEDIA_TYPE_YAML = MediaType.valueOf("application/yaml");
    public static final MediaType MEDIA_TYPE_YML = MediaType.valueOf("application/yml");

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer)
    {
        configurer
                .mediaType(MEDIA_TYPE_YML.getSubtype(), MEDIA_TYPE_YML)
                .mediaType(MEDIA_TYPE_YAML.getSubtype(), MEDIA_TYPE_YAML);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        YAMLMapper yamlMapper = new YAMLMapper();
        yamlMapper.registerModule(new Jdk8Module());
        yamlMapper.registerModule(new JavaTimeModule());
        yamlMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter yamlConverter = new MappingJackson2HttpMessageConverter(yamlMapper);
        yamlConverter.setSupportedMediaTypes(
                Arrays.asList(MEDIA_TYPE_YML, MEDIA_TYPE_YAML)
        );
        converters.add(yamlConverter);
    }

}
