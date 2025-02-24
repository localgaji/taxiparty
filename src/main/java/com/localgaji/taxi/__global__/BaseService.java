package com.localgaji.taxi.__global__;

public interface BaseService<Entity> {
    Entity findByIdOr404(Long id);

}
