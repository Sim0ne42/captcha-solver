package org.captcha.solver.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.NaturalId;

import java.sql.Types;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "captcha")
@NamedQuery(
    name = "CaptchaEntity.findRandom",
    query = "SELECT c FROM CaptchaEntity c ORDER BY RANDOM() LIMIT 1"
)
@NamedQuery(
    name = "CaptchaEntity.findAllIds",
    query = "SELECT c.id FROM CaptchaEntity c"
)
@NamedQuery(
    name = "CaptchaEntity.countPredicted",
    query = "SELECT COUNT(c) FROM CaptchaEntity c WHERE c.predictedValue IS NOT NULL"
)
@NamedQuery(
    name = "CaptchaEntity.countCorrectPredictions",
    query = "SELECT COUNT(c) FROM CaptchaEntity c WHERE c.predictedValue = c.label"
)
public class CaptchaEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, insertable = false, updatable = false)
  private Integer id;

  @Lob
  @JdbcTypeCode(Types.VARBINARY)
  @Column(name = "image", nullable = false, updatable = false)
  private byte[] image;

  @Column(name = "label", nullable = false, updatable = false)
  private String label;

  @Column(name = "predicted_value")
  private String predictedValue;

  @NaturalId
  @Column(name = "filename", nullable = false, updatable = false, unique = true)
  private String filename;

  @Column(name = "format", nullable = false, updatable = false)
  private String format;

  @Column(name = "width", nullable = false, updatable = false)
  private Integer width;

  @Column(name = "height", nullable = false, updatable = false)
  private Integer height;

  @Column(name = "sample", nullable = false, updatable = false)
  private Boolean sample;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  @Temporal(value = TemporalType.TIMESTAMP)
  private Instant createdAt;

}
