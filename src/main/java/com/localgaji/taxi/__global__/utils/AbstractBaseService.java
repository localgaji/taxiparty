package com.localgaji.taxi.__global__.utils;

import com.localgaji.taxi.__global__.exception.CustomException;
import com.localgaji.taxi.__global__.exception.ErrorType;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@NoArgsConstructor
public abstract class AbstractBaseService<Entity> implements BaseService<Entity> {

    private JpaRepository<Entity, Long> repository;

    protected AbstractBaseService(JpaRepository<Entity, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Entity findByIdOr404(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new CustomException(ErrorType.FORBIDDEN)
        );
    }
}
