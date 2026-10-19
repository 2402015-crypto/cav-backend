package mx.hotel.backend_cav.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import mx.hotel.backend_cav.entity.Nombrable;

@NoRepositoryBean
public interface NombreRepository<T extends Nombrable> extends JpaRepository<T, Integer> {

    List<T> findAllByOrderByNombreAsc();

    List<T> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}