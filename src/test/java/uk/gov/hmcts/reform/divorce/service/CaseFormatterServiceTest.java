package uk.gov.hmcts.reform.divorce.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.reform.divorce.mapper.DivorceCaseToAosCaseMapper;
import uk.gov.hmcts.reform.divorce.mapper.DivorceCaseToDaCaseMapper;
import uk.gov.hmcts.reform.divorce.mapper.DivorceCaseToDnCaseMapper;
import uk.gov.hmcts.reform.divorce.mapper.DivorceCaseToDnClarificationMapper;
import uk.gov.hmcts.reform.divorce.model.DivorceCaseWrapper;
import uk.gov.hmcts.reform.divorce.model.ccd.AosCaseData;
import uk.gov.hmcts.reform.divorce.model.ccd.CoreCaseData;
import uk.gov.hmcts.reform.divorce.model.ccd.DaCaseData;
import uk.gov.hmcts.reform.divorce.model.ccd.DnCaseData;
import uk.gov.hmcts.reform.divorce.model.ccd.DnRefusalCaseData;
import uk.gov.hmcts.reform.divorce.model.documentupdate.GeneratedDocumentInfo;
import uk.gov.hmcts.reform.divorce.model.usersession.DivorceSession;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseFormatterServiceTest {

    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private DataTransformer dataTransformer;

    @Mock
    private DataMapTransformer dataMapTransformer;

    @Mock
    private DivorceCaseToAosCaseMapper divorceCaseToAosCaseMapper;

    @Mock
    private DivorceCaseToDnCaseMapper divorceCaseToDnCaseMapper;

    @Mock
    private DivorceCaseToDaCaseMapper divorceCaseToDaCaseMapper;

    @Mock
    private CoreCaseDataDocumentService coreCaseDataDocumentService;

    @Mock
    private DivorceCaseToDnClarificationMapper divorceCaseToDnClarificationMapper;

    @InjectMocks
    private CaseFormatterService caseFormatterService;

    @Test
    public void shouldTransformDivorceSessionToCoreCaseDataFormat() {

        final DivorceSession divorceSession = new DivorceSession();
        final CoreCaseData expectedCaseData = new CoreCaseData();

        when(dataTransformer.transformDivorceCaseDataToCourtCaseData(divorceSession)).thenReturn(expectedCaseData);

        final CoreCaseData actualCaseData = caseFormatterService.transformToCoreCaseDataFormat(divorceSession);

        assertEquals(expectedCaseData, actualCaseData);

        verify(dataTransformer).transformDivorceCaseDataToCourtCaseData(divorceSession);
    }

    @Test
    public void shouldTransformDivorceSessionMapToCoreCaseDataFormatMap() {

        final Map<String, Object> divorceSession = emptyMap();
        final Map<String, Object> expectedCaseData = emptyMap();

        when(dataMapTransformer.transformDivorceCaseDataToCourtCaseData(divorceSession)).thenReturn(expectedCaseData);

        final Map<String, Object> actualCaseData = caseFormatterService.transformToCoreCaseDataFormat(divorceSession);

        assertThat(actualCaseData, is(expectedCaseData));

        verify(dataMapTransformer).transformDivorceCaseDataToCourtCaseData(divorceSession);
    }

    @Test
    public void shouldTransformCoreCaseDataToDivorceSession() {

        final CoreCaseData coreCaseData = new CoreCaseData();
        final DivorceSession expectedDivorceSession = new DivorceSession();

        when(dataTransformer.transformCoreCaseDataToDivorceCaseData(coreCaseData)).thenReturn(expectedDivorceSession);

        final DivorceSession actualDivorceSession = caseFormatterService.transformToDivorceSession(coreCaseData);

        assertEquals(expectedDivorceSession, actualDivorceSession);

        verify(dataTransformer).transformCoreCaseDataToDivorceCaseData(coreCaseData);
    }

    @Test
    public void shouldTransformCoreCaseDataMapToDivorceSessionMap() throws Exception {

        final CoreCaseData coreCaseData = new CoreCaseData();
        coreCaseData.setD8caseReference("123");
        coreCaseData.setD8MarriageDate("2021-01-14");

        final HashMap<String, Object> coreCaseDataMap = new HashMap<>();
        coreCaseDataMap.put("D8caseReference", "123");
        coreCaseDataMap.put("D8MarriageDate", "2021-01-14");

        final DivorceSession divorceSession = new DivorceSession();
        divorceSession.setCaseReference("123");
        divorceSession.setMarriageDate(INPUT_DATE_FORMAT.parse("2021-01-14"));

        final HashMap<String, Object> divorceSessionMap = new HashMap<>();
        divorceSessionMap.put("expires", 0L);
        divorceSessionMap.put("caseReference", "123");
        divorceSessionMap.put("marriageDate", "2021-01-14T00:00:00.000+0000");

        when(dataTransformer.transformCoreCaseDataToDivorceCaseData(eq(coreCaseData))).thenReturn(divorceSession);

        final Map<String, Object> actualDivorceSession = caseFormatterService.transformToDivorceSession(coreCaseDataMap);

        assertThat(actualDivorceSession, is(divorceSessionMap));

        verify(dataTransformer).transformCoreCaseDataToDivorceCaseData(coreCaseData);
    }

    @Test
    public void shouldAddDocumentsToCoreCaseDataMap() {

        final Map<String, Object> coreCaseData = emptyMap();
        final Map<String, Object> expectedCoreCaseData = emptyMap();
        final List<GeneratedDocumentInfo> generatedDocumentInfos = emptyList();

        when(coreCaseDataDocumentService.addDocuments(coreCaseData, generatedDocumentInfos)).thenReturn(expectedCoreCaseData);

        final Map<String, Object> updatedCaseData = caseFormatterService.addDocuments(coreCaseData, generatedDocumentInfos);

        assertThat(updatedCaseData, is(expectedCoreCaseData));

        verify(coreCaseDataDocumentService).addDocuments(coreCaseData, generatedDocumentInfos);
    }

    @Test
    public void shouldRemoveAllPetitionDocuments() {

        final Map<String, Object> coreCaseData = emptyMap();
        final Map<String, Object> expectedCoreCaseData = emptyMap();

        when(coreCaseDataDocumentService.removeAllPetitionDocuments(coreCaseData)).thenReturn(expectedCoreCaseData);

        final Map<String, Object> updatedCaseData = caseFormatterService.removeAllPetitionDocuments(coreCaseData);

        assertThat(updatedCaseData, is(expectedCoreCaseData));

        verify(coreCaseDataDocumentService).removeAllPetitionDocuments(coreCaseData);
    }

    @Test
    public void shouldRemoveAllDocumentsByType() {

        final Map<String, Object> coreCaseData = emptyMap();
        final Map<String, Object> expectedCoreCaseData = emptyMap();
        final String documentType = "type";

        when(coreCaseDataDocumentService.removeAllDocumentsByType(coreCaseData, documentType)).thenReturn(expectedCoreCaseData);

        final Map<String, Object> updatedCaseData = caseFormatterService.removeAllDocumentsByType(coreCaseData, documentType);

        assertThat(updatedCaseData, is(expectedCoreCaseData));

        verify(coreCaseDataDocumentService).removeAllDocumentsByType(coreCaseData, documentType);
    }

    @Test
    public void shouldReturnAosCaseData() {

        final DivorceSession divorceSession = mock(DivorceSession.class);
        final AosCaseData aosCaseData = mock(AosCaseData.class);

        when(divorceCaseToAosCaseMapper.divorceCaseDataToAosCaseData(divorceSession)).thenReturn(aosCaseData);

        assertEquals(aosCaseData, caseFormatterService.getAosCaseData(divorceSession));

        verify(divorceCaseToAosCaseMapper).divorceCaseDataToAosCaseData(divorceSession);
    }

    @Test
    public void shouldReturnAosCaseDataMap() {

        final DivorceSession divorceSession = new DivorceSession();
        final AosCaseData aosCaseData = new AosCaseData();

        when(divorceCaseToAosCaseMapper.divorceCaseDataToAosCaseData(any(DivorceSession.class))).thenReturn(aosCaseData);

        final Map<String, Object> actualAosCaseDataMap = caseFormatterService.getAosCaseData(convertToMap(divorceSession));

        assertThat(actualAosCaseDataMap, is(convertToMap(aosCaseData)));

        verify(divorceCaseToAosCaseMapper).divorceCaseDataToAosCaseData(any(DivorceSession.class));
    }

    @Test
    public void shouldReturnDnCaseData() {

        final DivorceSession divorceSession = mock(DivorceSession.class);
        final DnCaseData dnCaseData = mock(DnCaseData.class);

        when(divorceCaseToDnCaseMapper.divorceCaseDataToDnCaseData(divorceSession)).thenReturn(dnCaseData);

        assertEquals(dnCaseData, caseFormatterService.getDnCaseData(divorceSession));

        verify(divorceCaseToDnCaseMapper).divorceCaseDataToDnCaseData(divorceSession);
    }

    @Test
    public void shouldReturnDnCaseDataMap() {

        final DivorceSession divorceSession = new DivorceSession();
        final DnCaseData dnCaseData = new DnCaseData();

        when(divorceCaseToDnCaseMapper.divorceCaseDataToDnCaseData(any(DivorceSession.class))).thenReturn(dnCaseData);

        final Map<String, Object> actualDnCaseDataMap = caseFormatterService.getDnCaseData(convertToMap(divorceSession));

        assertThat(actualDnCaseDataMap, is(convertToMap(dnCaseData)));

        verify(divorceCaseToDnCaseMapper).divorceCaseDataToDnCaseData(any(DivorceSession.class));
    }

    @Test
    public void shouldReturnDaCaseData() {

        final DivorceSession divorceSession = mock(DivorceSession.class);
        final DaCaseData daCaseData = mock(DaCaseData.class);

        when(divorceCaseToDaCaseMapper.divorceCaseDataToDaCaseData(divorceSession)).thenReturn(daCaseData);

        assertEquals(daCaseData, caseFormatterService.getDaCaseData(divorceSession));

        verify(divorceCaseToDaCaseMapper).divorceCaseDataToDaCaseData(divorceSession);
    }

    @Test
    public void shouldReturnDaCaseDataMap() {

        final DivorceSession divorceSession = new DivorceSession();
        final DaCaseData daCaseData = new DaCaseData();

        when(divorceCaseToDaCaseMapper.divorceCaseDataToDaCaseData(any(DivorceSession.class))).thenReturn(daCaseData);

        final Map<String, Object> actualDaCaseDataMap = caseFormatterService.getDaCaseData(convertToMap(divorceSession));

        assertThat(actualDaCaseDataMap, is(convertToMap(daCaseData)));

        verify(divorceCaseToDaCaseMapper).divorceCaseDataToDaCaseData(any(DivorceSession.class));
    }

    @Test
    public void shouldReturnDnRefusalCaseData() {

        final DivorceCaseWrapper divorceCaseWrapper = new DivorceCaseWrapper(new CoreCaseData(), new DivorceSession());
        final DnRefusalCaseData dnRefusalCaseData = mock(DnRefusalCaseData.class);

        when(divorceCaseToDnClarificationMapper.divorceCaseDataToDnCaseData(divorceCaseWrapper)).thenReturn(dnRefusalCaseData);

        assertEquals(dnRefusalCaseData, caseFormatterService.getDnClarificationCaseData(divorceCaseWrapper));

        verify(divorceCaseToDnClarificationMapper).divorceCaseDataToDnCaseData(divorceCaseWrapper);
    }

    @Test
    public void shouldReturnDnRefusalCaseDataMap() {

        final DivorceCaseWrapper divorceCaseWrapper = new DivorceCaseWrapper(new CoreCaseData(), new DivorceSession());
        final DnRefusalCaseData dnRefusalCaseData = new DnRefusalCaseData();

        when(divorceCaseToDnClarificationMapper.divorceCaseDataToDnCaseData(any(DivorceCaseWrapper.class))).thenReturn(dnRefusalCaseData);

        final Map<String, Object> actualDnRefusalCaseDataMap =
            caseFormatterService.getDnClarificationCaseData(convertToMap(divorceCaseWrapper));

        assertThat(actualDnRefusalCaseDataMap, is(convertToMap(dnRefusalCaseData)));

        verify(divorceCaseToDnClarificationMapper).divorceCaseDataToDnCaseData(any(DivorceCaseWrapper.class));
    }

    private Map<String, Object> convertToMap(final Object object) {
        return objectMapper.convertValue(object, new TypeReference<>() {
        });
    }
}
