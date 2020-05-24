package com.piedel.piotr.configuration.service.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Etag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;

    private final String value;
}
