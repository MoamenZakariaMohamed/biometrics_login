package com.example.demo.infra;

import com.example.demo.domain.*;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.util.Base64UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WebAuthnService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatorRepository authenticatorRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    private final WebAuthnManager webAuthnManager;
    private final SecureRandom secureRandom = new SecureRandom();

    public WebAuthnService() {
        this.webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
    }

    public Map<String, Object> startRegistration(String username, String email, String displayName) {
        // Generate user handle
        byte[] userHandleBytes = new byte[64];
        secureRandom.nextBytes(userHandleBytes);
        String userHandle = Base64UrlUtil.encodeToString(userHandleBytes);

        // Create or get user
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new User(username, email, userHandle, displayName);
            userRepository.save(user);
        }

        // Generate challenge
        byte[] challengeBytes = new byte[32];
        secureRandom.nextBytes(challengeBytes);
        String challengeId = UUID.randomUUID().toString();
        String challengeData = Base64UrlUtil.encodeToString(challengeBytes);

        // Store challenge
        Challenge challenge = new Challenge();
        challenge.setChallengeId(challengeId);
        challenge.setChallengeData(challengeData);
        challenge.setUserHandle(userHandle);
        challenge.setChallengeType("registration");
        challengeRepository.save(challenge);

        // Create registration options
        Map<String, Object> options = new HashMap<>();
        options.put("challenge", challengeData);
        options.put("rp", Map.of("name", "MyApp", "id", "localhost"));
        options.put("user", Map.of(
                "id", userHandle,
                "name", username,
                "displayName", displayName
        ));
        options.put("pubKeyCredParams", List.of(
                Map.of("type", "public-key", "alg", -7), // ES256
                Map.of("type", "public-key", "alg", -257) // RS256
        ));
        options.put("authenticatorSelection", Map.of(
                "authenticatorAttachment", "platform",
                "userVerification", "required"
        ));
        options.put("attestation", "direct");
        options.put("timeout", 60000);
        options.put("challengeId", challengeId);

        return options;
    }

    public boolean finishRegistration(String challengeId, Map<String, Object> credential) {
        // Get challenge
        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElse(null);
        if (challenge == null || challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = userRepository.findByUserHandle(challenge.getUserHandle()).orElse(null);
        if (user == null) {
            return false;
        }

        try {
            // Save authenticator
            Authenticator authenticator = new Authenticator();
            authenticator.setCredentialId((String) credential.get("id"));
            authenticator.setPublicKey((String) credential.get("publicKey"));
            authenticator.setUser(user);
            authenticatorRepository.save(authenticator);

            // Clean up challenge
            challengeRepository.delete(challenge);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map startAuthentication() {
        // Generate challenge
        byte[] challengeBytes = new byte[32];
        secureRandom.nextBytes(challengeBytes);
        String challengeId = UUID.randomUUID().toString();
        String challengeData = Base64UrlUtil.encodeToString(challengeBytes);

        // Store challenge (without specific user)
        Challenge challenge = new Challenge();
        challenge.setChallengeId(challengeId);
        challenge.setChallengeData(challengeData);
        challenge.setUserHandle(null); // No specific user for quick login
        challenge.setChallengeType("authentication");
        challengeRepository.save(challenge);

        // Get all authenticators for resident credentials
        List<Authenticator> authenticators = authenticatorRepository.findAll();
        List<Map<String, Object>> allowCredentials = new ArrayList<>();
        for (Authenticator auth : authenticators) {
            allowCredentials.add(Map.of(
                    "type", "public-key",
                    "id", auth.getCredentialId(),
                    "transports", List.of("internal")
            ));
        }

        Map<String, Object> options = new HashMap<>();
        options.put("challenge", challengeData);
        options.put("timeout", 10);
        options.put("rpId", "localhost");
        options.put("allowCredentials", allowCredentials);
        options.put("userVerification", "required");
        options.put("challengeId", challengeId);

        return options;
    }

    public User finishAuthentication(String challengeId, Map<String, Object> credential) {
        // Get challenge
        Challenge challenge = challengeRepository.findByChallengeId(challengeId).orElse(null);
        if (challenge == null || challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        // Get authenticator
        Authenticator authenticator = authenticatorRepository.findByCredentialId((String) credential.get("id")).orElse(null);
        if (authenticator == null) {
            return null;
        }

        // Update last used
        authenticator.setLastUsed(LocalDateTime.now());
        authenticatorRepository.save(authenticator);

        // Clean up challenge
        challengeRepository.delete(challenge);

        return authenticator.getUser();
    }


    public static void main(String[] args) {
                int[] nums = {6,4,3,5,7,0,1};
                int n = nums.length;
                int ans=0;
                for(int i=0;i<n;i++){
                    ans ^= nums[i] ^ (i+1);
                }
                System.out.println(ans);
                int expectedSum = (n * (n + 1)) / 2;
                int actualSum = 0;

                for (int num : nums) {
                    actualSum += num;
                }

                int missingNumber = expectedSum - actualSum;
                System.out.println(missingNumber);

    }

}
