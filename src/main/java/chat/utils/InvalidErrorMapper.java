package chat.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InvalidErrorMapper {

    public static List<HashMap<String, Object>> map (BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors ()
                .stream ()
                .map (InvalidErrorMapper::map)
                .collect (Collectors.toList ());
    }

    public static HashMap<String, Object> map (FieldError e) {
        return new HashMap<> () {{
            put ("object", e.getObjectName ());
            put ("field", e.getField ());
            put ("text", e.getDefaultMessage ());
        }};
    }

    public static HashMap<String, Object> map(String field, String text){
        return new HashMap<> () {{
            put ("field", field);
            put ("text", text);
        }};
    }

}
