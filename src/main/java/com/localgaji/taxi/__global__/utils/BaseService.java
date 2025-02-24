package com.localgaji.taxi.__global__.utils;

public interface BaseService<Entity> {
    Entity findByIdOr404(Long id);

}
