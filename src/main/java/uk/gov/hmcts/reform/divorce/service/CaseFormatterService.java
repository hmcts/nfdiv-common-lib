package uk.gov.hmcts.reform.divorce.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CaseFormatterService {

    private final ObjectMapper objectMapper;
    private final DataTransformer dataTransformer;
    private final DataMapTransformer dataMapTransformer;
    private final DivorceCaseToAosCaseMapper divorceCaseToAosCaseMapper;
    private final DivorceCaseToDnCaseMapper divorceCaseToDnCaseMapper;
    private final DivorceCaseToDnClarificationMapper divorceCaseToDnClarificationMapper;
    private final DivorceCaseToDaCaseMapper divorceCaseToDaCaseMapper;
    private final CoreCaseDataDocumentService coreCaseDataDocumentService;

    public CoreCaseData transformToCoreCaseDataFormat(final DivorceSession divorceSession) {
        return dataTransformer.transformDivorceCaseDataToCourtCaseData(divorceSession);
    }

    public Map<String, Object> transformToCoreCaseDataFormat(Map<String, Object> divorceSessionMap) {
        return dataMapTransformer.transformDivorceCaseDataToCourtCaseData(divorceSessionMap);
    }

    public DivorceSession transformToDivorceSession(final CoreCaseData coreCaseData) {
        return dataTransformer.transformCoreCaseDataToDivorceCaseData(coreCaseData);
    }

    public Map<String, Object> transformToDivorceSession(final Map<String, Object> coreCaseDataMap) {
        return dataMapTransformer.transformCoreCaseDataToDivorceCaseData(coreCaseDataMap);
    }

    public Map<String, Object> addDocuments(final Map<String, Object> coreCaseData,
                                            final List<GeneratedDocumentInfo> generatedDocumentInfos) {
        return coreCaseDataDocumentService.addDocuments(coreCaseData, generatedDocumentInfos);
    }

    public Map<String, Object> removeAllPetitionDocuments(final Map<String, Object> coreCaseData) {
        return coreCaseDataDocumentService.removeAllPetitionDocuments(coreCaseData);
    }

    public Map<String, Object> removeAllDocumentsByType(final Map<String, Object> coreCaseData, final String documentType) {
        return coreCaseDataDocumentService.removeAllDocumentsByType(coreCaseData, documentType);
    }

    public AosCaseData getAosCaseData(final DivorceSession divorceSession) {
        return divorceCaseToAosCaseMapper.divorceCaseDataToAosCaseData(divorceSession);
    }

    public Map<String, Object> getAosCaseData(final Map<String, Object> divorceSessionMap) {
        final DivorceSession divorceSession = objectMapper.convertValue(divorceSessionMap, DivorceSession.class);
        final AosCaseData aosCaseData = getAosCaseData(divorceSession);
        return objectMapper.convertValue(aosCaseData, new TypeReference<>() {
        });
    }

    public DnCaseData getDnCaseData(final DivorceSession divorceSession) {
        return divorceCaseToDnCaseMapper.divorceCaseDataToDnCaseData(divorceSession);
    }

    public Map<String, Object> getDnCaseData(final Map<String, Object> divorceSessionMap) {
        final DivorceSession divorceSession = objectMapper.convertValue(divorceSessionMap, DivorceSession.class);
        final DnCaseData dnCaseData = getDnCaseData(divorceSession);
        return objectMapper.convertValue(dnCaseData, new TypeReference<>() {
        });
    }

    public DnRefusalCaseData getDnClarificationCaseData(final DivorceCaseWrapper divorceCaseWrapper) {
        return divorceCaseToDnClarificationMapper.divorceCaseDataToDnCaseData(divorceCaseWrapper);
    }

    public Map<String, Object> getDnClarificationCaseData(final Map<String, Object> divorceCaseWrapperMap) {
        final DivorceCaseWrapper divorceCaseWrapper = objectMapper.convertValue(divorceCaseWrapperMap, DivorceCaseWrapper.class);
        final DnRefusalCaseData dnRefusalCaseData = getDnClarificationCaseData(divorceCaseWrapper);
        return objectMapper.convertValue(dnRefusalCaseData, new TypeReference<>() {
        });
    }

    public DaCaseData getDaCaseData(final DivorceSession divorceSession) {
        return divorceCaseToDaCaseMapper.divorceCaseDataToDaCaseData(divorceSession);
    }

    public Map<String, Object> getDaCaseData(final Map<String, Object> divorceSessionMap) {
        final DivorceSession divorceSession = objectMapper.convertValue(divorceSessionMap, DivorceSession.class);
        final DaCaseData daCaseData = getDaCaseData(divorceSession);
        return objectMapper.convertValue(daCaseData, new TypeReference<>() {
        });
    }
}
