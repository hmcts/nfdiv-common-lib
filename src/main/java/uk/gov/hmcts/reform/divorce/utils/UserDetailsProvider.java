package uk.gov.hmcts.reform.divorce.utils;

import uk.gov.hmcts.reform.divorce.model.UserDetails;

import java.util.Optional;

public interface UserDetailsProvider {

    Optional<UserDetails> getUserDetails(String token);

}
