package com.piedel.piotr.configuration.service.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @deprecated Deprecated cause i am not using this anymore - should delete but I don't
 * want to lose the idea of this - maybe I will figure it out how to use it properly
 * Remove this if not
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@Deprecated
public class Etag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final long id;

    private final String value;
}
