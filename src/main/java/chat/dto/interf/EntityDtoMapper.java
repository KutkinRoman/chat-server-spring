package chat.dto.interf;

public interface EntityDtoMapper<E, D> {

    D map (E entity);
}
