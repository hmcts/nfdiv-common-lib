package uk.gov.hmcts.reform.divorce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.divorce.mapper.DocumentCollectionDocumentRequestMapper;
import uk.gov.hmcts.reform.divorce.model.ccd.CollectionMember;
import uk.gov.hmcts.reform.divorce.model.ccd.Document;
import uk.gov.hmcts.reform.divorce.model.ccd.DocumentLink;
import uk.gov.hmcts.reform.divorce.model.documentupdate.DocumentUpdateRequest;
import uk.gov.hmcts.reform.divorce.model.documentupdate.GeneratedDocumentInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.divorce.model.DocumentType.PETITION;

@RunWith(MockitoJUnitRunner.class)
public class CoreCaseDataDocumentServiceTest {

    private static final String D8_DOCUMENTS_GENERATED_CCD_FIELD = "D8DocumentsGenerated";
    private static final String HAL_BINARY_RESPONSE_CONTEXT_PATH =
        (String) ReflectionTestUtils.getField(
            DocumentCollectionDocumentRequestMapper.class, "HAL_BINARY_RESPONSE_CONTEXT_PATH");
    private static final String PDF_FILE_EXTENSION =
        (String) ReflectionTestUtils.getField(
            DocumentCollectionDocumentRequestMapper.class, "PDF_FILE_EXTENSION");

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private DocumentCollectionDocumentRequestMapper documentCollectionDocumentRequestMapper;

    @InjectMocks
    private CoreCaseDataDocumentService coreCaseDataDocumentService;

    @Test
    public void shouldReturnSameCaseDataIfGeneratedDocumentInfoIsNull() {

        final Map<String, Object> coreCaseData = emptyMap();

        final Map<String, Object> actual = coreCaseDataDocumentService.addDocuments(coreCaseData, null);

        assertThat(actual, is(coreCaseData));
    }

    @Test
    public void shouldReturnSameCaseDataIfGeneratedDocumentInfoIsEmpty() {

        final Map<String, Object> expected = emptyMap();
        final Map<String, Object> coreCaseData = emptyMap();

        final Map<String, Object> actual = coreCaseDataDocumentService.addDocuments(coreCaseData, emptyList());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldAddDocumentsWhenNoD8DocumentsInCaseData() {

        final String url1 = "url1";
        final String documentType1 = "petition";
        final String fileName1 = "fileName1";
        final String url2 = "url1";
        final String documentType2 = "aos";
        final String fileName2 = "fileName2";

        final GeneratedDocumentInfo generatedDocumentInfo1 = createGeneratedDocument(url1, documentType1, fileName1);
        final GeneratedDocumentInfo generatedDocumentInfo2 = createGeneratedDocument(url2, documentType2, fileName2);

        final CollectionMember<Document> document1 = createCollectionMemberDocument(url1, documentType1, fileName1);
        final CollectionMember<Document> document2 = createCollectionMemberDocument(url2, documentType2, fileName2);

        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo1)).thenReturn(document1);
        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo2)).thenReturn(document2);

        final Map<String, Object> expected =
            singletonMap(D8_DOCUMENTS_GENERATED_CCD_FIELD, asList(document1, document2));

        final Map<String, Object> input = new HashMap<>();

        final Map<String, Object> actual = coreCaseDataDocumentService.addDocuments(input,
            asList(generatedDocumentInfo1, generatedDocumentInfo2));

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldAddDocumentsWhenConflictingD8DocumentsExistsInCaseData() {

        final String url1 = "url1";
        final String documentType1 = "petition";
        final String fileName1 = "fileName1";
        final String url2 = "url2";
        final String documentType2 = "aos";
        final String fileName2 = "fileName2";

        final String url3 = "url3";
        final String documentType3 = "aos1";
        final String fileName3 = "fileName3";

        final String url4 = "url4";
        final String documentType4 = "aos";
        final String fileName4 = "fileName4";

        final GeneratedDocumentInfo generatedDocumentInfo1 = createGeneratedDocument(url1, documentType1, fileName1);
        final GeneratedDocumentInfo generatedDocumentInfo2 = createGeneratedDocument(url2, documentType2, fileName2);

        final CollectionMember<Document> document1 = createCollectionMemberDocument(url1, documentType1, fileName1);
        final CollectionMember<Document> document2 = createCollectionMemberDocument(url2, documentType2, fileName2);
        final CollectionMember<Document> document3 = createCollectionMemberDocument(url3, documentType3, fileName3);
        final CollectionMember<Document> document4 = createCollectionMemberDocument(url4, documentType4, fileName4);

        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo1)).thenReturn(document1);
        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo2)).thenReturn(document2);

        final Map<String, Object> expected =
            singletonMap(D8_DOCUMENTS_GENERATED_CCD_FIELD, asList(document3, document1, document2));

        final Map<String, Object> input = new HashMap<>();
        input.put(D8_DOCUMENTS_GENERATED_CCD_FIELD, asList(document3, document4));

        final DocumentUpdateRequest documentUpdateRequest = new DocumentUpdateRequest();
        documentUpdateRequest.setDocuments(asList(generatedDocumentInfo1, generatedDocumentInfo2));
        documentUpdateRequest.setCaseData(input);

        final Map<String, Object> actual = coreCaseDataDocumentService.addDocuments(
            input,
            asList(generatedDocumentInfo1, generatedDocumentInfo2));

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldAddDocumentsWhenMultipleGenericD8DocumentsExistsInCaseData() {

        final String url1 = "url1";
        final String documentType1 = "petition";
        final String fileName1 = "fileName1";
        final String url2 = "url2";
        final String documentType2 = "aos";
        final String fileName2 = "fileName2";

        final String url3 = "url3";
        final String documentType3 = "other";
        final String fileName3 = "fileName3";

        final String url4 = "url4";
        final String documentType4 = "aos";
        final String fileName4 = "fileName4";

        final String url5 = "url5";
        final String documentType5 = "other";
        final String fileName5 = "fileName5";

        final GeneratedDocumentInfo generatedDocumentInfo1 = createGeneratedDocument(url1, documentType1, fileName1);
        final GeneratedDocumentInfo generatedDocumentInfo2 = createGeneratedDocument(url2, documentType2, fileName2);
        final GeneratedDocumentInfo generatedDocumentInfo3 = createGeneratedDocument(url3, documentType3, fileName3);

        final CollectionMember<Document> document1 = createCollectionMemberDocument(url1, documentType1, fileName1);
        final CollectionMember<Document> document2 = createCollectionMemberDocument(url2, documentType2, fileName2);
        final CollectionMember<Document> document3 = createCollectionMemberDocument(url3, documentType3, fileName3);
        final CollectionMember<Document> document4 = createCollectionMemberDocument(url4, documentType4, fileName4);
        final CollectionMember<Document> document5 = createCollectionMemberDocument(url5, documentType5, fileName5);

        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo1)).thenReturn(document1);
        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo2)).thenReturn(document2);
        when(documentCollectionDocumentRequestMapper.map(generatedDocumentInfo3)).thenReturn(document3);

        final Map<String, Object> expected =
            singletonMap(D8_DOCUMENTS_GENERATED_CCD_FIELD, asList(document5, document1, document2, document3));

        final Map<String, Object> input = new HashMap<>();
        input.put(D8_DOCUMENTS_GENERATED_CCD_FIELD, asList(document4, document5));

        final DocumentUpdateRequest documentUpdateRequest = new DocumentUpdateRequest();
        documentUpdateRequest.setDocuments(asList(generatedDocumentInfo1, generatedDocumentInfo2, generatedDocumentInfo3));
        documentUpdateRequest.setCaseData(input);

        final Map<String, Object> actual = coreCaseDataDocumentService.addDocuments(input,
            asList(generatedDocumentInfo1, generatedDocumentInfo2, generatedDocumentInfo3));

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenCallingAddDocumentsIfCoreCaseDataIsNull() {

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coreCaseDataDocumentService.addDocuments(null, emptyList());
        });

        assertThat(exception.getMessage(), is("Existing case data must not be null."));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenCallingRemoveAllPetitionDocumentsIfCoreCaseDataIsNull() {

        final Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coreCaseDataDocumentService.removeAllPetitionDocuments(null);
        });

        assertThat(exception.getMessage(), is("Existing case data must not be null."));
    }

    @Test
    public void shouldDoNothingIfThereAreNoPetitions() {

        final Map<String, Object> caseData = caseDataMapWithDocumentsCollection(asList("not this 1", "not that 1"));

        final Map<String, Object> updatedCaseData = coreCaseDataDocumentService.removeAllPetitionDocuments(caseData);

        final List<CollectionMember<Document>> documents = (getDocumentsFrom(updatedCaseData));

        assertThat(documents.size(), is(2));
        assertThat(documents.get(0).getValue().getDocumentType(), is("not this 1"));
        assertThat(documents.get(1).getValue().getDocumentType(), is("not that 1"));
    }

    @Test
    public void shouldOnlyRemoveSinglePetition() {

        final Map<String, Object> caseData = caseDataMapWithDocumentsCollection(asList(PETITION, "no", "no no"));

        final Map<String, Object> updatedCaseData = coreCaseDataDocumentService.removeAllPetitionDocuments(caseData);

        final List<CollectionMember<Document>> documents = getDocumentsFrom(updatedCaseData);

        assertThat(documents.size(), is(2));
        assertThat(documents.get(0).getValue().getDocumentType(), is("no"));
        assertThat(documents.get(1).getValue().getDocumentType(), is("no no"));
    }

    @Test
    public void shouldOnlyRemovePetitions() {

        final Map<String, Object> caseData = caseDataMapWithDocumentsCollection(
            asList(PETITION, "not this", PETITION)
        );

        final Map<String, Object> updatedCaseData = coreCaseDataDocumentService.removeAllPetitionDocuments(caseData);

        final List<CollectionMember<Document>> documents = getDocumentsFrom(updatedCaseData);

        assertThat(documents.size(), is(1));
        assertThat(documents.get(0).getValue().getDocumentType(), is("not this"));
    }

    @Test
    public void shouldRemovePetitionAndReturnEmptyListOfDocuments() {

        final Map<String, Object> caseData = new HashMap<>(
            singletonMap(
                D8_DOCUMENTS_GENERATED_CCD_FIELD,
                singletonList(createCollectionMemberDocument("url3", PETITION, "X"))
            )
        );

        final Map<String, Object> updatedCaseData = coreCaseDataDocumentService.removeAllPetitionDocuments(caseData);

        final List<CollectionMember<Document>> documents = getDocumentsFrom(updatedCaseData);

        assertThat(documents, empty());
    }

    private GeneratedDocumentInfo createGeneratedDocument(String url, String documentType, String fileName) {
        return GeneratedDocumentInfo.builder()
            .url(url)
            .documentType(documentType)
            .fileName(fileName)
            .build();
    }

    private CollectionMember<Document> createCollectionMemberDocument(String url, String documentType,
                                                                      String fileName) {
        final CollectionMember<Document> collectionMember = new CollectionMember<>();
        final Document document = new Document();
        final DocumentLink documentLink = new DocumentLink();

        documentLink.setDocumentUrl(url);
        documentLink.setDocumentBinaryUrl(url + HAL_BINARY_RESPONSE_CONTEXT_PATH);
        documentLink.setDocumentFilename(fileName + PDF_FILE_EXTENSION);
        document.setDocumentFileName(fileName);
        document.setDocumentLink(documentLink);
        document.setDocumentType(documentType);

        collectionMember.setValue(document);

        return collectionMember;
    }

    private Map<String, Object> caseDataMapWithDocumentsCollection(List<String> documentTypes) {
        return new HashMap<>(
            singletonMap(
                D8_DOCUMENTS_GENERATED_CCD_FIELD,
                documentTypes.stream()
                    .map(type -> createCollectionMemberDocument("url", type, "x"))
                    .collect(Collectors.toList()))
        );
    }

    @SuppressWarnings("unchecked")
    private List<CollectionMember<Document>> getDocumentsFrom(Map<String, Object> updatedCaseData) {
        return (List<CollectionMember<Document>>) updatedCaseData.get(D8_DOCUMENTS_GENERATED_CCD_FIELD);
    }
}