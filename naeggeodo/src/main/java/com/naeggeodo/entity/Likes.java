package com.naeggeodo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Likes {

    @Id
    private Long id;

    private int count;

    public void updateCount() {
        ++this.count;
    }
}