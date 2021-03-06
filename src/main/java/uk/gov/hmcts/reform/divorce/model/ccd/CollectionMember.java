package uk.gov.hmcts.reform.divorce.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude()
@Data
public class CollectionMember<T> {
    private String id;
    private T value;

    public static <T> CollectionMember<T> buildCollectionMember(T value) {
        CollectionMember collectionMember = new CollectionMember();
        collectionMember.setValue(value);
        return collectionMember;
    }
}
