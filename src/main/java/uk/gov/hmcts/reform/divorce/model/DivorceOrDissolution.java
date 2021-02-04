package uk.gov.hmcts.reform.divorce.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DivorceOrDissolution {

    DIVORCE("divorce"),
    DISSOLUTION("dissolution");

    private final String value;

    DivorceOrDissolution(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
