package uk.gov.hmcts.reform.divorce.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DivorceSessionDateDeserializerTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String EXPECTED_EXCEPTION_MESSAGE = "Unparseable date: "
        + "\"14/01/2021T00:00:00\". "
        + "Supported formats: "
        + "\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\", "
        + "\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\", "
        + "\"yyyy-MM-dd'T'HH:mm:ss.SSS\", "
        + "\"EEE, dd MMM yyyy HH:mm:ss zzz\", "
        + "\"yyyy-MM-dd\"";

    @Test
    public void shouldDeserializeFromCorrectFormats() throws Exception {

        final Date expectedDate = DATE_FORMAT.parse("2021-01-14");
        final DivorceSessionDateDeserializer divorceSessionDateDeserializer = new DivorceSessionDateDeserializer(Date.class);
        final JsonParser jsonParser = mock(JsonParser.class);
        final ObjectCodec objectCodec = mock(ObjectCodec.class);
        final JsonNode jsonNode = mock(JsonNode.class);

        when(jsonParser.getCodec()).thenReturn(objectCodec);
        when(objectCodec.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.textValue())
            .thenReturn("2021-01-14T00:00:00.000+0000")
            .thenReturn("2021-01-14T00:00:00.000Z")
            .thenReturn("2021-01-14T00:00:00.000")
            .thenReturn("Thu, 14 Jan 2021 00:00:00 UTC")
            .thenReturn("2021-01-14");

        Date actualDate = divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
        assertThat(actualDate, is(expectedDate));

        actualDate = divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
        assertThat(actualDate, is(expectedDate));

        actualDate = divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
        assertThat(actualDate, is(expectedDate));

        actualDate = divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
        assertThat(actualDate, is(expectedDate));

        actualDate = divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
        assertThat(actualDate, is(expectedDate));
    }

    @Test
    public void shouldReturnNullDateWhenNullValue() throws Exception {

        final DivorceSessionDateDeserializer divorceSessionDateDeserializer = new DivorceSessionDateDeserializer(Date.class);
        final JsonParser jsonParser = mock(JsonParser.class);
        final ObjectCodec objectCodec = mock(ObjectCodec.class);
        final JsonNode jsonNode = mock(JsonNode.class);

        when(jsonParser.getCodec()).thenReturn(objectCodec);
        when(objectCodec.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.textValue()).thenReturn(null);

        final Date actualDate = divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
        assertNull(actualDate);
    }

    @Test
    public void shouldThrowExceptionIfFormatNotSupported() throws Exception {

        final DivorceSessionDateDeserializer divorceSessionDateDeserializer = new DivorceSessionDateDeserializer(Date.class);
        final JsonParser jsonParser = mock(JsonParser.class);
        final ObjectCodec objectCodec = mock(ObjectCodec.class);
        final JsonNode jsonNode = mock(JsonNode.class);

        when(jsonParser.getCodec()).thenReturn(objectCodec);
        when(objectCodec.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.textValue())
            .thenReturn("14/01/2021T00:00:00");

        try {
            divorceSessionDateDeserializer.deserialize(jsonParser, mock(DeserializationContext.class));
            fail();
        } catch (final JsonParseException e) {
            assertThat(e.getMessage(), is(EXPECTED_EXCEPTION_MESSAGE));
        }
    }
}