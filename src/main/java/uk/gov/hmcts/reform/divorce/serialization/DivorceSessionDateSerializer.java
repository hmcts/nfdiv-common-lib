package uk.gov.hmcts.reform.divorce.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DivorceSessionDateSerializer extends StdSerializer<Date> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public DivorceSessionDateSerializer() {
        this(Date.class);
    }

    public DivorceSessionDateSerializer(final Class t) {
        super(t);
    }

    @Override
    public void serialize(final Date date,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(DATE_FORMAT.format(date));
    }
}
