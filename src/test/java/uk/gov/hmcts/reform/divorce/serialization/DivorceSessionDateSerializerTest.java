package uk.gov.hmcts.reform.divorce.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DivorceSessionDateSerializerTest {

    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void shouldSerializeDateToCorrectFormat() throws Exception {

        final Date date = INPUT_DATE_FORMAT.parse("2021-01-14");
        final JsonGenerator jsonGenerator = mock(JsonGenerator.class);

        new DivorceSessionDateSerializer().serialize(date, jsonGenerator, mock(SerializerProvider.class));

        verify(jsonGenerator).writeString("2021-01-14T00:00:00.000+0000");
    }
}