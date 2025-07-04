package com.common.easton_portal.service;

import com.common.core.base.helper.JsonHelper;
import com.common.core.web.permission.PermissionSystem;
import com.common.core.web.permission.RightAnnotation;
import com.common.core.web.security.jwt.HttpJwtHelper;
import com.common.easton_portal.constants.SignerNameConstant;
import com.common.easton_portal.core.EncryptedProvider;
import com.common.easton_portal.core.SignerProvider;
import com.common.easton_portal.data.SecurityTokenInfo;
import com.common.easton_portal.entity.OAuthEntity;
import com.common.easton_portal.entity.UserEntity;
import com.common.easton_portal.model.UserModel;
import com.common.easton_portal.repos.OAuthRepository;
import com.common.easton_portal.repos.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    public static class OAuthTokenResponse {
        @JsonProperty
        public String access_token;
        @JsonProperty public String refresh_token;
        @JsonProperty public String id_token;
    }

    public static class OAuthTokenRequest {
        @JsonProperty public String grant_type;
        @JsonProperty public String client_id;
        @JsonProperty public String client_secret;
    }

    public static class OAuthAuthorizeCodeRequest extends OAuthTokenRequest {
        @JsonProperty public String redirect_uri;
        @JsonProperty public String code;
        public OAuthAuthorizeCodeRequest() { grant_type = "authorization_code"; }
    }


    @Autowired private OAuthRepository mOAuthRepository;
    @Autowired private PermissionSystem<RightAnnotation> mPermissionSystem;
    @Autowired private UserRepository mUserRepository;
    @Autowired private EncryptedProvider mEncryptor;

    @Autowired @Qualifier(SignerNameConstant.authentication)
    private SignerProvider mSignerProvider;


    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SecurityTokenInfo authenticateWithOAuth(long id, String authCode, String redirectUri) throws Exception {
        var domain = mOAuthRepository.findById(id).orElseThrow(() -> new Exception("OAuth domain not supported"));
        var response = generateAuthenticationToken(domain, authCode, redirectUri);

        var user = updateUserEntity(response, domain);
        if (user == null) return new SecurityTokenInfo();
        var rights = calculateRights(user, !hasDomainAdmin(), mPermissionSystem);
        return createTokenInfo(new UserModel(user.getId(), user.getDomain().getId(), user.getUsername(), response.id_token, rights), response.id_token, null);
    }


    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public SecurityTokenInfo refreshToken(UserModel user) throws Exception {
        var entity = mUserRepository.findById(user.getUuid()).orElseThrow(() -> new Exception("User not found"));
        var rights = calculateRights(entity, false, mPermissionSystem);
        var newModel = new UserModel(entity, user.getIdToken(), rights);
        return createTokenInfo(newModel, user.getIdToken(), null);
    }

    private UserEntity updateUserEntity(OAuthTokenResponse response, OAuthEntity domain) throws Exception {
        var info = response.id_token.split("\\.");
        var user_info = new String(Base64.getDecoder().decode(info[1]));
        var data = JsonHelper.fromString(user_info);
        if (data == null || data.get("sub") == null) throw new Exception("User data not found");

        var userId = data.get("sub").asText();
        var user = mUserRepository.findByDomainUserIdAndDomain(userId, domain).orElse(null);

        if (user != null && user.isDisable()) return null;
        if (user == null) {
            user = UserEntity.builder()
                    .administrator(domain.isDefaultAdministrator())
                    .build();
            user.getRoles().addAll(domain.getDefaultRoles());
        }

        user.setDomainUserId(userId);
        user.setDomain(domain);
        user.setAccountName(data.get("preferred_username") == null ? "" : data.get("preferred_username").asText(null));
        user.setUsername(data.get("name") == null ? "" : data.get("name").asText(""));
        user.setEmail(data.get("email") != null ? data.get("email").asText() : "");
        mUserRepository.saveAndFlush(user);
        return user;
    }

    private List<GrantedAuthority> calculateRights(UserEntity user, boolean temporaryAdmin, PermissionSystem<RightAnnotation> system) {
        return user.isAdministrator() || temporaryAdmin ?
                system.getStream().map(s -> (GrantedAuthority) new SimpleGrantedAuthority(s.databaseKey)).toList() :
                user.getRoles().stream().flatMap(s -> s.getRights().stream()).distinct().map(s ->
                        (GrantedAuthority) new SimpleGrantedAuthority(s)).toList();
    }

    private OAuthTokenResponse generateAuthenticationToken(OAuthEntity domain, String authCode, String redirectUri) throws Exception {
        var clientId = domain.getClientId();
        var clientSecret = mEncryptor.decryptFromBase64(domain.getClientSecret());
        var headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);

        try {
            var request = new OAuthAuthorizeCodeRequest();
            request.client_id = clientId;
            request.client_secret = clientSecret;
            request.redirect_uri = redirectUri;
            request.code = authCode;

            var result = postWithForm(domain.getTokenUrl(), headers, JsonHelper.toTree(request));
            return JsonHelper.parse(result, OAuthTokenResponse.class);
        } catch (Exception ex) {
            throw new Exception("OAuth server authentication process fails");
        }
    }

    private SecurityTokenInfo createTokenInfo(UserModel model, String idToken, Duration durationPeriod) throws Exception {
        var defaultValue = mSignerProvider.getExpirationPeriod().toMillis();
        if (durationPeriod == null) durationPeriod = Duration.ofMillis(defaultValue);
        var duration = Math.max(durationPeriod.toMillis(), defaultValue);

        var token = HttpJwtHelper.create(mSignerProvider.getWriteSigner(), String.valueOf(model.getUuid()),
                Duration.ofMillis(duration), model.getClaims());
        return new SecurityTokenInfo(token, System.currentTimeMillis() + duration, idToken);
    }

    private static JsonNode postWithForm(String url, HttpHeaders headers, Object body) {
        var restTemplate = new RestTemplate();
        if (headers == null) headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var objectMapper = new ObjectMapper();
        var map = objectMapper.convertValue(body, new TypeReference<Map<String, String>>() {});
        var parameters = new LinkedMultiValueMap<String, String>();
        parameters.setAll(map);
        var request = new HttpEntity<>(parameters, headers);
        return JsonHelper.fromString(restTemplate.postForObject(url, request, String.class));
    }

    private boolean hasDomainAdmin() {
        var domains = mOAuthRepository.findAll();
        return domains.stream().anyMatch(s -> {
            if (s.isDisable()) return false;
            var users = s.getUsers();
            if (users.size() == 0) return false;
            return users.stream().anyMatch(UserEntity::isAdministrator);
        });
    }

}
