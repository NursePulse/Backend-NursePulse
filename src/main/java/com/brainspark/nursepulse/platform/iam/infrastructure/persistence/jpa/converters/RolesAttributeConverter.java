package com.brainspark.nursepulse.platform.iam.infrastructure.persistence.jpa.converters;

import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA converter for the {@code roles.name} column that tolerates role names stored
 * in the database that no longer exist in {@link Roles}.
 *
 * With the default {@code @Enumerated(EnumType.STRING)} mapping, Hibernate calls
 * {@code Roles.valueOf(...)} directly while hydrating the entity and lets any
 * {@link IllegalArgumentException} propagate — which crashes the eager
 * {@code UserPersistenceEntity.roles} fetch (and therefore every read of a user) the
 * moment a role gets renamed or merged without a matching data migration. This has
 * already happened once in this project's history.
 *
 * Converting to {@code null} instead lets the row load; callers reading a user's roles
 * are responsible for skipping entries with a null name (see
 * {@code UserQueryServiceImpl}).
 */
@Slf4j
@Converter
public class RolesAttributeConverter implements AttributeConverter<Roles, String> {

    @Override
    public String convertToDatabaseColumn(Roles attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public Roles convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return Roles.valueOf(dbData);
        } catch (IllegalArgumentException exception) {
            log.warn("Ignoring unknown/legacy role name '{}' found in the roles table", dbData);
            return null;
        }
    }
}
