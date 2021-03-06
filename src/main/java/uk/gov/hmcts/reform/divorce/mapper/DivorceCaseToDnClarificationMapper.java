package uk.gov.hmcts.reform.divorce.mapper;

import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import uk.gov.hmcts.reform.divorce.model.DivorceCaseWrapper;
import uk.gov.hmcts.reform.divorce.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.model.ccd.DnRefusalCaseData;
import uk.gov.hmcts.reform.divorce.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.model.usersession.DivorceSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = DocumentCollectionCCDFormatMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class DivorceCaseToDnClarificationMapper {

    private static final String CLARIFICATION_STRING = "Clarification %s: %s";
    private static final String DOCUMENT_COMMENT = "Document";

    @Mapping(source = "divorceSession.files", target = "documentsUploadedDnClarification")
    public abstract DnRefusalCaseData divorceCaseDataToDnCaseData(DivorceCaseWrapper divorceCaseWrapper);

    @AfterMapping
    protected void mapDnClarificationResponse(DivorceCaseWrapper divorceCaseWrapper,
                                              @MappingTarget DnRefusalCaseData result) {

        List<CollectionMember<String>> clarificationReasons =
            Optional.ofNullable(divorceCaseWrapper.getCaseData().getDnClarificationResponse())
                .orElse(new ArrayList<>());

        DivorceSession divorceSession = divorceCaseWrapper.getDivorceSession();

        if (divorceSession.getClarificationResponse() != null) {
            CollectionMember<String> clarificationResponse = new CollectionMember<>();
            clarificationResponse.setValue(String.format(CLARIFICATION_STRING,
                clarificationReasons.size() + 1, divorceSession.getClarificationResponse()
            ));

            clarificationReasons.add(clarificationResponse);

            result.setDnClarificationResponse(clarificationReasons);
        }
    }

    @AfterMapping
    protected void mapDnClarificationUploadAnyOtherDocuments(DivorceCaseWrapper divorceCaseWrapper,
                                                             @MappingTarget DnRefusalCaseData result) {

        List<CollectionMember<String>> uploadAnyOtherDocumentsList =
            Optional.ofNullable(divorceCaseWrapper.getCaseData().getDnClarificationUploadDocuments())
                .orElse(new ArrayList<>());

        DivorceSession divorceSession = divorceCaseWrapper.getDivorceSession();

        if (divorceSession.getUploadAnyOtherDocuments() != null) {
            CollectionMember<String> uploadAnyOtherDocuments = new CollectionMember<>();
            uploadAnyOtherDocuments.setValue(String.format(CLARIFICATION_STRING,
                uploadAnyOtherDocumentsList.size() + 1, divorceSession.getUploadAnyOtherDocuments()
            ));

            uploadAnyOtherDocumentsList.add(uploadAnyOtherDocuments);

            result.setDnClarificationUploadDocuments(uploadAnyOtherDocumentsList);
        }
    }

    @AfterMapping
    protected void mapDocumentsUploadedDnClarification(DivorceCaseWrapper divorceCaseWrapper,
                                                       @MappingTarget DnRefusalCaseData result) {

        List<CollectionMember<Document>> clarificationDocuments =
            Optional.ofNullable(divorceCaseWrapper.getCaseData().getDocumentsUploadedDnClarification())
                .orElse(new ArrayList<>());

        int clarificationNumber =
            Optional.ofNullable(result.getDnClarificationResponse()).orElse(new ArrayList<>())
                .size();

        // New documents are already added to the result from the @Mapping annotation on the constructor
        // This can then be used in the AfterMapping
        if (result.getDocumentsUploadedDnClarification() != null) {
            result.getDocumentsUploadedDnClarification().forEach(document ->
                document.getValue().setDocumentComment(String.format(CLARIFICATION_STRING,
                    clarificationNumber, DOCUMENT_COMMENT)));
            clarificationDocuments.addAll(result.getDocumentsUploadedDnClarification());

            result.setDocumentsUploadedDnClarification(clarificationDocuments);
        }
    }

    @AfterMapping
    protected void mapClarificationDigital(DivorceCaseWrapper divorceCaseWrapper,
                                           @MappingTarget DnRefusalCaseData result) {
        result.setClarificationDigital(translateToStringYesNo(
            divorceCaseWrapper.getDivorceSession().getClarificationDigital()));
    }

    private String translateToStringYesNo(final String value) {
        if (Objects.isNull(value)) {
            return null;
        }

        return BooleanUtils.toStringYesNo(BooleanUtils.toBoolean(value)).toUpperCase(Locale.ENGLISH);
    }
}
