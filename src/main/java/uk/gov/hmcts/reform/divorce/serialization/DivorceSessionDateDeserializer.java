package uk.gov.hmcts.reform.divorce.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class DivorceSessionDateDeserializer extends StdDeserializer<Date> {

    private static final SimpleDateFormat[] DATE_FORMATS = new SimpleDateFormat[]{
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"),
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"),
        new SimpleDateFormat("yyyy-MM-dd")
    };

    public DivorceSessionDateDeserializer() {
        this(Date.class);
    }

    public DivorceSessionDateDeserializer(Class<?> theClass) {
        super(theClass);
    }

    @Override
    public Date deserialize(final JsonParser jsonParser,
                            final DeserializationContext deserializationContext) throws IOException {

        final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        final String date = jsonNode.textValue();

        if (null == date) {
            return null;
        }

        for (final SimpleDateFormat dateFormat : DATE_FORMATS) {
            try {
                return dateFormat.parse(date);
            } catch (final ParseException e) {
                //Do nothing and try the next format
            }
        }

        throw new JsonParseException(jsonParser,
            "Unparseable date: \"" + date + "\". Supported formats: " + getDatePatterns());

    }

    private String getDatePatterns() {
        return stream(DATE_FORMATS)
            .map(SimpleDateFormat::toPattern)
            .collect(joining("\", \"", "\"", "\""));
    }
}
