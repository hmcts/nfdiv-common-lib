package uk.gov.hmcts.reform.divorce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.model.usersession.DivorceSession;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.divorce.utils.ObjectMapperTestUtil.getObjectMapperInstance;

@RunWith(MockitoJUnitRunner.class)
public class DataMapTransformerTest {

    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Spy
    private final ObjectMapper objectMapper = getObjectMapperInstance();

    @Mock
    private DataTransformer dataTransformer;

    @InjectMocks
    private DataMapTransformer dataMapTransformer;

    private DivorceSession divorceSession;
    private Map<String, Object> divorceSessionMap;

    private CoreCaseData coreCaseData;
    private Map<String, Object> coreCaseDataMap;

    @Before
    public void setup() throws Exception {

        divorceSession = new DivorceSession();
        divorceSession.setCaseReference("123");
        divorceSession.setMarriageDate(INPUT_DATE_FORMAT.parse("2021-01-14"));

        divorceSessionMap = new HashMap<>();
        divorceSessionMap.put("expires", 0L);
        divorceSessionMap.put("caseReference", "123");
        divorceSessionMap.put("marriageDate", "2021-01-14T00:00:00.000+0000");

        coreCaseData = new CoreCaseData();
        coreCaseData.setD8caseReference("123");
        coreCaseData.setD8MarriageDate("2021-01-14");

        coreCaseDataMap = new HashMap<>();
        coreCaseDataMap.put("D8caseReference", "123");
        coreCaseDataMap.put("D8MarriageDate", "2021-01-14");

        when(dataTransformer.transformDivorceCaseDataToCourtCaseData(eq(divorceSession))).thenReturn(coreCaseData);
        when(dataTransformer.transformCoreCaseDataToDivorceCaseData(eq(coreCaseData))).thenReturn(divorceSession);
    }

    @Test
    public void shouldCallAdequateMapperForTransformingDivorceCaseDataIntoCoreCaseData() {
        final Map<String, Object> returnedCoreCaseData = dataMapTransformer.transformDivorceCaseDataToCourtCaseData(divorceSessionMap);

        assertThat(returnedCoreCaseData, equalTo(coreCaseDataMap));
        verify(dataTransformer).transformDivorceCaseDataToCourtCaseData(eq(divorceSession));
    }

    @Test
    public void shouldCallAdequateMapperForTransformingCoreCaseDataIntoDivorceCaseData() {
        final Map<String, Object> returnedDivorceCaseData = dataMapTransformer.transformCoreCaseDataToDivorceCaseData(coreCaseDataMap);

        assertThat(returnedDivorceCaseData, equalTo(divorceSessionMap));
        verify(dataTransformer).transformCoreCaseDataToDivorceCaseData(eq(coreCaseData));
    }
}
