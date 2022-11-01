package guru.springframework.services;

import guru.springframework.domain.UnitOfMeasure;
import reactor.core.publisher.Flux;

/**
 * Created by jt on 6/28/17.
 */
public interface UnitOfMeasureService {
    Flux<UnitOfMeasure> listAllUoms();
}
