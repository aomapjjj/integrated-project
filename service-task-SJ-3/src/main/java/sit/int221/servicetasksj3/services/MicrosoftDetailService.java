package sit.int221.servicetasksj3.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;
import sit.int221.servicetasksj3.sharedatabase.entities.MicrosoftUser;

import java.util.HashMap;
import java.util.Map;


@Service
public class MicrosoftDetailService {

    public MicrosoftUser getMicrosoftByEmail(String email, String token) {
        try {
            String graphApiUrl = "https://graph.microsoft.com/v1.0/users/" + email;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<String> response = restTemplate.exchange(graphApiUrl, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(response.getBody(), Map.class);
            MicrosoftUser microsoftUser = new MicrosoftUser();
            microsoftUser.setOid((String) map.get("id"));
            microsoftUser.setDisplayName((String) map.get("displayName"));
            microsoftUser.setEmail((String) map.get("mail"));
            return microsoftUser;
        } catch (Exception e) {
            return null;
        }
    }

}


