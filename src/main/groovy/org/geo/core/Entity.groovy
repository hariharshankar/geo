package org.geo.core

/**
 @author: Harihar Shankar, 10/3/13 8:55 AM
 */

public abstract class Entity {

    private Integer id;
    private String name;

    public Entity(Integer id) {
        this.id = id
    }

    public Integer getId() {
        return this.id
    }

    public String getName() {
        return this.name
    }

    public void setName(String name) {
        this.name = name
    }
}