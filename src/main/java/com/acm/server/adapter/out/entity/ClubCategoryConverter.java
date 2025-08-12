package com.acm.server.adapter.out.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = true)
public class ClubCategoryConverter implements AttributeConverter<ClubCategory, String> {

    @Override
    public String convertToDatabaseColumn(ClubCategory attribute) {
        if (attribute == null) return null;
        return attribute.getLabel(); // 한글 label을 DB에 저장
    }

    @Override
    public ClubCategory convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Arrays.stream(ClubCategory.values())
                     .filter(c -> c.getLabel().equals(dbData))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Unknown category: " + dbData));
    }
}
