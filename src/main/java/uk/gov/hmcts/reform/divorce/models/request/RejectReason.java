package uk.gov.hmcts.reform.divorce.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RejectReason {

    @JsonProperty("RejectReasonType")
    private String rejectReasonType;

    @JsonProperty("RejectReasonText")
    private String rejectReasonText;

}
