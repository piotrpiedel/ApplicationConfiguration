package com.piedel.piotr.configuration.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;


@Entity
@Setter
@Getter
@AllArgsConstructor()
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String key;
    private String value;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp creationDate;

    @ManyToOne()
    private ClientVersion clientVersion;

    public String getClient() {
        return clientVersion.getClient();
    }

    public String getVersion() {
        return clientVersion.getVersion();
    }

    public long getCreationDateTimeAsTimestamp() {
        return creationDate.getTime();
    }
}
