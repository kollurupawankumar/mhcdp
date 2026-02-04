package com.opencode.release.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
public class YamlReader {


    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());


    public <T> T read(Resource resource, Class<T> type) {
        try (var in = resource.getInputStream()) {
            return yamlMapper.readValue(in, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse YAML: " + resource, e);
        }
    }
}
