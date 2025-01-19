package home.work.finance.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class FileRepository {
    protected final ObjectMapper objectMapper;

    public FileRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        init();
    }

    abstract protected void init();

    protected <T> void saveDataToFile(String filePath, List<T> data) throws IOException {
        objectMapper.writeValue(new File(filePath), data);
    }

    protected <T> List<T> loadDataFromFile(String filePath, Class<T> type) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }
}