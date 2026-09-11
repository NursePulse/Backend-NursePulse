package com.brainspark.nursepulse.platform.iam.infrastructure.persistence.jpa.entities;

import com.brainspark.nursepulse.platform.iam.domain.model.valueobjects.Roles;
import com.brainspark.nursepulse.platform.iam.infrastructure.persistence.jpa.converters.RolesAttributeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for IAM roles.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RolePersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = RolesAttributeConverter.class)
    @Column(name = "name", nullable = false, unique = true, length = 20)
    private Roles name;
}
