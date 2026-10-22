package mx.hotel.backend_cav.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.hotel.backend_cav.entity.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    Optional<Vehiculo> findByPlacaIgnoreCase(String placa);

    @Modifying
    @Query("update Vehiculo v set v.marca = null where v.marca.id = :marcaId")
    int desvincularMarca(@Param("marcaId") Integer marcaId);

    @Modifying
    @Query("update Vehiculo v set v.color = null where v.color.id = :colorId")
    int desvincularColor(@Param("colorId") Integer colorId);
}