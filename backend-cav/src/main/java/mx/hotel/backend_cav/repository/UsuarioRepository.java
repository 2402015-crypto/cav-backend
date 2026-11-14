package mx.hotel.backend_cav.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.entity.EstadoUsuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuarioIgnoreCase(String usuario);

    boolean existsByUsuarioIgnoreCase(String usuario);

    List<Usuario> findAllByOrderByIdAsc();

    long countByEstado(EstadoUsuario estado);
}