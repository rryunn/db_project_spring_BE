package com.acm.server.adapter.out.external.redis;

import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.acm.server.application.club.port.out.ClubRedisPort;
import com.acm.server.domain.Club;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.AcademicClub;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClubRedis implements ClubRedisPort {

    private static final String ALL_CLUBS_KEY = "club:list:all";
    private static final String CENTRAL_CLUB_KEY_PREFIX = "club:central:";
    private static final String ACADEMIC_CLUB_KEY_PREFIX = "club:academic:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // ========= 전체 목록 =========

    @Override
    public Optional<List<Club>> getAllClubs() {
        String json = redisTemplate.opsForValue().get(ALL_CLUBS_KEY);
        if (json == null) return Optional.empty();

        try {
            List<Club> clubs = objectMapper.readValue(
                    json,
                    new TypeReference<List<Club>>() {}
            );
            return Optional.of(clubs);
        } catch (Exception e) {
            log.warn("[ClubStore] Failed to deserialize all clubs cache", e);
            return Optional.empty();
        }
    }

    @Override
    public void saveAllClubs(List<Club> clubs) {
        try {
            String json = objectMapper.writeValueAsString(clubs);
            redisTemplate.opsForValue().set(ALL_CLUBS_KEY, json);
        } catch (JsonProcessingException e) {
            log.warn("[ClubStore] Failed to serialize all clubs for cache", e);
        }
    }

    @Override
    public void evictAllClubs() {
        redisTemplate.delete(ALL_CLUBS_KEY);
    }

    // ========= 중앙 동아리 =========

    @Override
    public Optional<CentralClub> getCentralClub(Long clubId) {
        String key = centralKey(clubId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();

        try {
            CentralClub club = objectMapper.readValue(json, CentralClub.class);
            return Optional.of(club);
        } catch (Exception e) {
            log.warn("[ClubStore] Failed to deserialize central club cache. clubId={}", clubId, e);
            return Optional.empty();
        }
    }

    @Override
    public void saveCentralClub(CentralClub club) {
        String key = centralKey(club.getId());
        try {
            String json = objectMapper.writeValueAsString(club);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            log.warn("[ClubStore] Failed to serialize central club for cache. clubId={}", club.getId(), e);
        }
    }

    @Override
    public void evictCentralClub(Long clubId) {
        redisTemplate.delete(centralKey(clubId));
    }

    private String centralKey(Long clubId) {
        return CENTRAL_CLUB_KEY_PREFIX + clubId;
    }

    // ========= 학술 동아리 =========

    @Override
    public Optional<AcademicClub> getAcademicClub(Long clubId) {
        String key = academicKey(clubId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();

        try {
            AcademicClub club = objectMapper.readValue(json, AcademicClub.class);
            return Optional.of(club);
        } catch (Exception e) {
            log.warn("[ClubStore] Failed to deserialize academic club cache. clubId={}", clubId, e);
            return Optional.empty();
        }
    }

    @Override
    public void saveAcademicClub(AcademicClub club) {
        String key = academicKey(club.getId());
        try {
            String json = objectMapper.writeValueAsString(club);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            log.warn("[ClubStore] Failed to serialize academic club for cache. clubId={}", club.getId(), e);
        }
    }

    @Override
    public void evictAcademicClub(Long clubId) {
        redisTemplate.delete(academicKey(clubId));
    }

    private String academicKey(Long clubId) {
        return ACADEMIC_CLUB_KEY_PREFIX + clubId;
    }
}
