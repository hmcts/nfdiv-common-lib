package uk.gov.hmcts.reform.divorce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Builder
public class UserDetails {

    private final String id;
    private final String email;
    private final String forename;
    private final String surname;
    private final String authToken;
    private final List<String> roles;

}
