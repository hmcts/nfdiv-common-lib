package uk.gov.hmcts.reform.divorce.utils;

import org.junit.Test;
import uk.gov.hmcts.reform.divorce.model.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OidcUserDetailsProviderTest {

    private final UserDetailsProvider detailsProvider = new OidcUserDetailsProvider();

    @Test
    public void getInvalidUserDetails() {
        Optional<UserDetails> result = detailsProvider.getUserDetails("invalid JWT");

        assertTrue(result.isEmpty());
    }

    @Test
    public void getValidUserDetails() {
        Optional<UserDetails> result = detailsProvider.getUserDetails("eyJ0eXAiOiJKV1QiLCJraWQiOiIxZXIwV1J3Z0lPVEFGb2pFNHJDL2ZiZUt1M0k9IiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiRUhzRjFPQTlQNm9qUFEtMXFTeTFTQSIsInN1YiI6ImhtY3RzLmZhY3RAZ21haWwuY29tIiwiYXVkaXRUcmFja2luZ0lkIjoiZDkwOWIzYWEtYTlmMC00YWRkLWI1ODUtNDJjZGNiZTBmNjRlLTEzMTA1Njc1Iiwicm9sZXMiOlsiZmFjdC1hZG1pbiJdLCJpc3MiOiJodHRwczovL2Zvcmdlcm9jay1hbS5zZXJ2aWNlLmNvcmUtY29tcHV0ZS1pZGFtLWFhdDIuaW50ZXJuYWw6ODQ0My9vcGVuYW0vb2F1dGgyL3JlYWxtcy9yb290L3JlYWxtcy9obWN0cyIsInRva2VuTmFtZSI6ImlkX3Rva2VuIiwiZ2l2ZW5fbmFtZSI6IkZhY3QiLCJ1aWQiOiJlZDllY2E1MS1iZjljLTQxYzgtOGUxNi1hZWRiNGZkMTY0MmMiLCJhdWQiOiJmYWN0X2FkbWluIiwiY19oYXNoIjoieVlzVEFuSWZMczNsdjRSZ1Q4aDdnZyIsImFjciI6IjAiLCJhenAiOiJmYWN0X2FkbWluIiwiYXV0aF90aW1lIjoxNjEwMDE5NTk2LCJuYW1lIjoiRmFjdCBBZG1pbiIsInJlYWxtIjoiL2htY3RzIiwiZXhwIjoxNjEwMDIzMTgxLCJ0b2tlblR5cGUiOiJKV1RUb2tlbiIsImZhbWlseV9uYW1lIjoiQWRtaW4iLCJpYXQiOjE2MTAwMTk1ODF9.gMcIIfirm9ovZWAwtZQWctoSB-VxNFIBwMcQTJxFzPMu14UtTNIsx6Sj2C-YbwOOyM23dtZ_fFs1n7ZltL5l4YqkWJgnLtXX0leare2_nHEhc_yq8kBm0YL7IcJjXqOmfEOy7H4mLH9zA3popVx2AXFDpjUeUoE4fiHBTJs2uBNJUaxWguPO22iDs8Pu5cgOaVdQfUHcnZXojbJ82SCjfUcGiyaS4O949Cx2IpTtz4uo3IS1DRYtez22EZgwJ33vVVb4cEp8ZQPrMikzgACk1sj7wqhHUs1tR7QsJJA8UoHbZmm7C9zVrx31Zr75mnxGyTa10YDqwXqctEredDLkgw");

        assertFalse(result.isEmpty());

        UserDetails details = result.get();

        assertEquals("hmcts.fact@gmail.com", details.getEmail());
        assertEquals(List.of("fact-admin"), details.getRoles());
    }
}