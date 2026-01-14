package za.co.hpsc.web.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class DataUtil {
    public static class JsonJava8DateBuilder extends JsonJava8DateMapper.Builder {
        public JsonJava8DateBuilder(JsonMapper m) {
            super(m);
            this.addModule(new ParameterNamesModule());
            this.addModule(new Jdk8Module());
            this.addModule(new JavaTimeModule());

        }
    }

    public static class JsonJava8DateMapper extends JsonMapper {
        public static Builder builder() {
            return new JsonJava8DateBuilder(new JsonMapper());
        }
    }

    public static class CsvMapper extends ObjectMapper {
    }
}
