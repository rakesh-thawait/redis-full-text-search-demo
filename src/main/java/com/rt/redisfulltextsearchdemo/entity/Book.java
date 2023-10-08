package com.rt.redisfulltextsearchdemo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;


@Data
@RedisHash(timeToLive = 3600)
public class Book implements Serializable {

    @Id
    String id;
    String name;
    String isbn;
    Author author;

    // Do not save the entry in cache
    @Transient
    int weight;

}
