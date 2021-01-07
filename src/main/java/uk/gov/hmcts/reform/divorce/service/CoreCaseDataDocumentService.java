package uk.gov.hmcts.reform.divorce.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.divorce.mapper.DocumentCollectionDocumentRequestMapper;
import uk.gov.hmcts.reform.divorce.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.model.documentupdate.GeneratedDocumentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static uk.gov.hmcts.reform.divorce.model.DocumentType.PETITION;

@Service
@RequiredArgsConstructor
public class CoreCaseDataDocumentService {

    private static final String D8_DOCUMENTS_GENERATED_CCD_FIELD = "D8DocumentsGenerated";
    private static final String GENERIC_DOCUMENT_TYPE = "other";

    private final ObjectMapper objectMapper;
    private final DocumentCollectionDocumentRequestMapper documentCollectionDocumentRequestMapper;

    public Map<String, Object> removeAllPetitionDocuments(final Map<String, Object> coreCaseData) {
        return removeAllDocumentsByType(coreCaseData, PETITION);
    }

    public Map<String, Object> addDocuments(final Map<String, Object> coreCaseData,
                                            final List<GeneratedDocumentInfo> generatedDocumentInfos) {

        if (coreCaseData == null) {
            throw new IllegalArgumentException("Existing case data must not be null.");
        }

        if (isNotEmpty(generatedDocumentInfos)) {
            final List<CollectionMember<Document>> resultDocuments = new ArrayList<>();

            final List<CollectionMember<Document>> newDocuments =
                generatedDocumentInfos.stream()
                    .map(documentCollectionDocumentRequestMapper::map)
                    .collect(Collectors.toList());

            final Set<String> newDocumentsTypes = generatedDocumentInfos.stream()
                .map(GeneratedDocumentInfo::getDocumentType)
                .collect(Collectors.toSet());

            final List<CollectionMember<Document>> documentsGenerated =
                objectMapper.convertValue(coreCaseData.get(D8_DOCUMENTS_GENERATED_CCD_FIELD),
                    new TypeReference<>() {
                    });

            if (isNotEmpty(documentsGenerated)) {
                final List<CollectionMember<Document>> existingDocuments = documentsGenerated.stream()
                    .filter(documentCollectionMember ->
                        GENERIC_DOCUMENT_TYPE.equals(documentCollectionMember.getValue().getDocumentType())
                            || !newDocumentsTypes.contains(documentCollectionMember.getValue().getDocumentType())
                    )
                    .collect(Collectors.toList());

                resultDocuments.addAll(existingDocuments);
            }

            resultDocuments.addAll(newDocuments);
            coreCaseData.put(D8_DOCUMENTS_GENERATED_CCD_FIELD, resultDocuments);
        }

        return coreCaseData;
    }

    public Map<String, Object> removeAllDocumentsByType(final Map<String, Object> coreCaseData, final String documentType) {

        if (coreCaseData == null) {
            throw new IllegalArgumentException("Existing case data must not be null.");
        }

        final List<CollectionMember<Document>> allDocuments =
            objectMapper.convertValue(coreCaseData.get(D8_DOCUMENTS_GENERATED_CCD_FIELD),
                new TypeReference<>() {
                });

        if (isNotEmpty(allDocuments)) {
            allDocuments.removeIf(documents -> isDocumentType(documents, documentType));
            coreCaseData.replace(D8_DOCUMENTS_GENERATED_CCD_FIELD, allDocuments);
        }

        return coreCaseData;
    }

    private boolean isDocumentType(final CollectionMember<Document> document, final String documentType) {
        return document.getValue().getDocumentType().equalsIgnoreCase(documentType);
    }
}
