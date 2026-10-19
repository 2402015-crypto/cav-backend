package mx.hotel.backend_cav.service;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.NombreRequest;
import mx.hotel.backend_cav.dto.NombreResponse;
import mx.hotel.backend_cav.entity.Nombrable;
import mx.hotel.backend_cav.exception.BadRequestException;
import mx.hotel.backend_cav.exception.ConflictException;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.NombreRepository;

public abstract class AbstractNombreService<T extends Nombrable> {

    private final NombreRepository<T> repository;
    private final Supplier<T> factory;
    private final String recurso;

    protected AbstractNombreService(NombreRepository<T> repository, Supplier<T> factory, String recurso) {
        this.repository = repository;
        this.factory = factory;
        this.recurso = recurso;
    }

    @Transactional(readOnly = true)
    public List<NombreResponse> listar() {
        return repository.findAllByOrderByNombreAsc().stream().map(NombreResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public NombreResponse obtener(Integer id) {
        return NombreResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<NombreResponse> sugerir(String consulta) {
        String nombre = normalizarConsulta(consulta);
        return repository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre).stream()
                .map(NombreResponse::from)
                .toList();
    }

    @Transactional
    public NombreResponse crear(NombreRequest request) {
        String nombre = normalizar(request);
        validarDisponible(nombre, null);

        T entidad = factory.get();
        entidad.setNombre(nombre);
        return NombreResponse.from(repository.save(entidad));
    }

    @Transactional
    public NombreResponse actualizar(Integer id, NombreRequest request) {
        T entidad = buscar(id);
        String nombre = normalizar(request);
        validarDisponible(nombre, id);
        entidad.setNombre(nombre);
        return NombreResponse.from(entidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        repository.delete(buscar(id));
    }

    private T buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(recurso + " no encontrado"));
    }

    private void validarDisponible(String nombre, Integer idActual) {
        boolean duplicado = idActual == null
                ? repository.existsByNombreIgnoreCase(nombre)
                : repository.existsByNombreIgnoreCaseAndIdNot(nombre, idActual);
        if (duplicado) {
            throw new ConflictException("El nombre ya está registrado para " + recurso.toLowerCase());
        }
    }

    private String normalizar(NombreRequest request) {
        if (request == null || request.nombre() == null || request.nombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        return request.nombre().trim();
    }

    private String normalizarConsulta(String consulta) {
        if (consulta == null || consulta.isBlank()) {
            throw new BadRequestException("El parámetro q es obligatorio");
        }
        return consulta.trim();
    }
}