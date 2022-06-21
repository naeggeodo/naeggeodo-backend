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
    private Long count;

    public void updateCount(){
        this.count = ++this.count;
    }
}