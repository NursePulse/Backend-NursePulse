package com.brainspark.nursepulse.platform.shared.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Abstract base class for JPA entities with automatic audit tracking capabilities.
 *
 * This abstract entity serves as a mapped superclass for all auditable domain entities
 * that require automatic tracking of creation and modification timestamps.
 *
 * Features:
 * - Automatic generation of primary key (Identity strategy)
 * - Automatic capture of creation timestamp (read-only after creation)
 * - Automatic capture of last modification timestamp (updated on any change)
 * - Uses Spring Data's AuditingEntityListener for automatic timestamp management
 *
 * This class is configured as a {@link MappedSuperclass}, meaning it will not be
 * mapped to a database table itself, but its fields will be inherited and persisted
 * in all concrete entity subclasses.
 *
 * Usage in this project:
 * Concrete entity classes should extend this class to automatically inherit auditing functionality. <b>Example:</b>
 * <pre>
 * @Entity
 * @Table (name = "users")
 * public class User extends AuditableAbstractPersistenceEntity {
 *     private String name;
 *     // ... (other fields)
 * }
 * </pre>
 *
 * @see MappedSuperclass
 * @see AuditingEntityListener
 * @see CreatedDate
 * @see LastModifiedDate
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractPersistenceEntity {


    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;


    @LastModifiedDate
    @Column(nullable = false)
    private Date updateAt;
}
