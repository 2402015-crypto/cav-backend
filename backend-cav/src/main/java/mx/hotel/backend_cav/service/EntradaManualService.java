package mx.hotel.backend_cav.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.hotel.backend_cav.dto.EntradaLookupResponse;
import mx.hotel.backend_cav.dto.EntradaManualRequest;
import mx.hotel.backend_cav.dto.EntradaManualResponse;
import mx.hotel.backend_cav.entity.ColorVehiculo;
import mx.hotel.backend_cav.entity.Conductor;
import mx.hotel.backend_cav.entity.DatosVisitante;
import mx.hotel.backend_cav.entity.EstatusVisita;
import mx.hotel.backend_cav.entity.Empresa;
import mx.hotel.backend_cav.entity.MarcaVehiculo;
import mx.hotel.backend_cav.entity.MetodoRegistro;
import mx.hotel.backend_cav.entity.RegistroAcceso;
import mx.hotel.backend_cav.entity.TipoVisitante;
import mx.hotel.backend_cav.entity.TipoMovimiento;
import mx.hotel.backend_cav.entity.Usuario;
import mx.hotel.backend_cav.entity.Vehiculo;
import mx.hotel.backend_cav.entity.Transportadora;
import mx.hotel.backend_cav.exception.BadRequestException;
import mx.hotel.backend_cav.exception.ResourceNotFoundException;
import mx.hotel.backend_cav.repository.ColorVehiculoRepository;
import mx.hotel.backend_cav.repository.ConductorRepository;
import mx.hotel.backend_cav.repository.DatosVisitanteRepository;
import mx.hotel.backend_cav.repository.EstatusVisitaRepository;
import mx.hotel.backend_cav.repository.EmpresaRepository;
import mx.hotel.backend_cav.repository.MarcaVehiculoRepository;
import mx.hotel.backend_cav.repository.RegistroAccesoRepository;
import mx.hotel.backend_cav.repository.TipoVisitanteRepository;
import mx.hotel.backend_cav.repository.TransportadoraRepository;
import mx.hotel.backend_cav.repository.UsuarioRepository;
import mx.hotel.backend_cav.repository.VehiculoRepository;

@Service
public class EntradaManualService {

    private final VehiculoRepository vehiculoRepository;
    private final MarcaVehiculoRepository marcaRepository;
    private final ColorVehiculoRepository colorRepository;
    private final ConductorRepository conductorRepository;
    private final RegistroAccesoRepository registroRepository;
    private final DatosVisitanteRepository datosRepository;
    private final TipoVisitanteRepository tipoRepository;
    private final EstatusVisitaRepository estatusRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ApplicationTimeService timeService;
    private final TransportadoraRepository transportadoraRepository;

    public EntradaManualService(
            VehiculoRepository vehiculoRepository,
            MarcaVehiculoRepository marcaRepository,
            ColorVehiculoRepository colorRepository,
            ConductorRepository conductorRepository,
            RegistroAccesoRepository registroRepository,
            DatosVisitanteRepository datosRepository,
            TipoVisitanteRepository tipoRepository,
            EstatusVisitaRepository estatusRepository,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            ApplicationTimeService timeService,
            TransportadoraRepository transportadoraRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.marcaRepository = marcaRepository;
        this.colorRepository = colorRepository;
        this.conductorRepository = conductorRepository;
        this.registroRepository = registroRepository;
        this.datosRepository = datosRepository;
        this.tipoRepository = tipoRepository;
        this.estatusRepository = estatusRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.timeService = timeService;
        this.transportadoraRepository = transportadoraRepository;
    }

    @Transactional(readOnly = true)
    public EntradaLookupResponse buscar(String placa) {
        String placaNormalizada = normalizar(placa, "La placa es obligatoria");
        return vehiculoRepository.findByPlacaIgnoreCase(placaNormalizada)
                .map(vehiculo -> datosPrevios(vehiculo, placaNormalizada))
                .orElseGet(() -> new EntradaLookupResponse(false, placaNormalizada, null, null, null, null, null, null, null, null, null, null));
    }

    @Transactional
    public EntradaManualResponse registrar(EntradaManualRequest request, UserDetails userDetails) {
        String placa = normalizar(request.placa(), "La placa es obligatoria").toUpperCase(Locale.ROOT);
        boolean proveedor = "proveedores".equalsIgnoreCase(request.tipoRegistro());
        boolean transportadoraRegistro = "transportadoras".equalsIgnoreCase(request.tipoRegistro());
        MarcaVehiculo marca = obtenerMarca(request.marca(), !proveedor);
        ColorVehiculo color = obtenerColor(request.color(), !proveedor);
        Empresa empresa = proveedor ? obtenerEmpresa(request.empresa()) : null;
        Transportadora transportadora = transportadoraRegistro ? obtenerTransportadora(request.transportadora()) : null;
        Vehiculo vehiculo = vehiculoRepository.findByPlacaIgnoreCase(placa).orElseGet(Vehiculo::new);
        vehiculo.setPlaca(placa);
        vehiculo.setMarca(marca);
        vehiculo.setColor(color);
        vehiculo.setActivo(true);
        vehiculo = vehiculoRepository.save(vehiculo);

        Usuario usuario = usuarioRepository.findByUsuarioIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        Conductor conductor = conductorRepository.findFirstByNombreIgnoreCase(request.nombreCompleto().trim())
                .orElseGet(Conductor::new);
        conductor.setNombre(request.nombreCompleto().trim());
        conductor = conductorRepository.save(conductor);

        RegistroAcceso registro = new RegistroAcceso();
        registro.setUsuario(usuario);
        registro.setVehiculo(vehiculo);
        registro.setConductor(conductor);
        registro.setEmpresa(empresa);
        registro.setTransportadora(transportadora);
        registro.setTipoVisitante(resolverTipo(request.tipoRegistro()));
        registro.setEstatus(resolverEstatus());
        registro.setMetodoRegistro(MetodoRegistro.Manual);
        if (transportadoraRegistro) {
            registro.setTipoMovimiento(resolverMovimiento(request.tipoMovimiento()));
        }
        registro.setPlacaRegistrada(placa);
        registro.setFechaEntrada(request.fechaEntrada() != null ? request.fechaEntrada() : timeService.now());
        registro.setObservaciones(blankToNull(request.observaciones()));
        registro = registroRepository.save(registro);

        DatosVisitante datos = new DatosVisitante();
        datos.setRegistro(registro);
        if ("general".equalsIgnoreCase(request.tipoRegistro())) {
            datos.setDepartamento(blankToNull(request.area()));
        } else if (proveedor) {
            datos.setAreaDestino(blankToNull(request.destino()));
        } else if (transportadoraRegistro) {
            datos.setVillaHuesped(blankToNull(request.villa()));
            datos.setNumeroPax(request.numeroPax());
        } else if ("taxis".equalsIgnoreCase(request.tipoRegistro())) {
            datos.setDestino(blankToNull(request.destino()));
        } else {
            datos.setAreaDestino(blankToNull(request.area()));
        }
        datosRepository.save(datos);
        return new EntradaManualResponse(registro.getId(), placa, registro.getFechaEntrada());
    }

    private EntradaLookupResponse datosPrevios(Vehiculo vehiculo, String placa) {
        return registroRepository.findTopByVehiculoPlacaIgnoreCaseOrderByFechaEntradaDesc(placa)
                .map(registro -> {
                        DatosVisitante datos = datosRepository.findByRegistro_Id(registro.getId()).orElse(null);
                            boolean proveedor = registro.getTipoVisitante() != null
                            && registro.getTipoVisitante().getNombre().toLowerCase(Locale.ROOT).contains("proveedor");
                            boolean transportadora = registro.getTipoVisitante() != null
                                && registro.getTipoVisitante().getNombre().toLowerCase(Locale.ROOT).contains("transportadora");
                        String area = datos == null ? null : proveedor ? datos.getAreaDestino()
                            : datos.getDepartamento() != null ? datos.getDepartamento() : datos.getAreaDestino();
                            return new EntradaLookupResponse(true, placa,
                            registro.getConductor() == null ? null : registro.getConductor().getNombre(), area,
                            registro.getEmpresa() == null ? null : registro.getEmpresa().getNombre(),
                                registro.getTransportadora() == null ? null : registro.getTransportadora().getNombre(),
                            vehiculo.getMarca() == null ? null : vehiculo.getMarca().getNombre(),
                                vehiculo.getColor() == null ? null : vehiculo.getColor().getNombre(),
                                transportadora && registro.getTipoMovimiento() != null ? registro.getTipoMovimiento().name() : null,
                                transportadora && datos != null ? datos.getVillaHuesped() : null,
                                transportadora && datos != null ? datos.getNumeroPax() : null,
                                registro.getFechaEntrada());
                })
                        .orElseGet(() -> new EntradaLookupResponse(true, placa, null, null, null, null,
                        vehiculo.getMarca() == null ? null : vehiculo.getMarca().getNombre(),
                            vehiculo.getColor() == null ? null : vehiculo.getColor().getNombre(), null, null, null, null));
    }

    private MarcaVehiculo obtenerMarca(String nombre, boolean obligatoria) {
        if (nombre == null || nombre.isBlank()) {
            if (obligatoria) throw new BadRequestException("La marca es obligatoria");
            return null;
        }
        String valor = capitalizarPrimeraLetra(nombre.trim());
        return marcaRepository.findByNombreIgnoreCase(valor).orElseGet(() -> {
            MarcaVehiculo nueva = new MarcaVehiculo();
            nueva.setNombre(valor);
            return marcaRepository.save(nueva);
        });
    }

    private ColorVehiculo obtenerColor(String nombre, boolean obligatoria) {
        if (nombre == null || nombre.isBlank()) {
            if (obligatoria) throw new BadRequestException("El color es obligatorio");
            return null;
        }
        String valor = capitalizarPrimeraLetra(nombre.trim());
        return colorRepository.findByNombreIgnoreCase(valor).orElseGet(() -> {
            ColorVehiculo nuevo = new ColorVehiculo();
            nuevo.setNombre(valor);
            return colorRepository.save(nuevo);
        });
    }

    private Empresa obtenerEmpresa(String nombre) {
        String valor = capitalizarPrimeraLetra(normalizar(nombre, "La empresa es obligatoria"));
        return empresaRepository.findByNombreIgnoreCase(valor).orElseGet(() -> {
            Empresa nueva = new Empresa();
            nueva.setNombre(valor);
            return empresaRepository.save(nueva);
        });
    }

    private Transportadora obtenerTransportadora(String nombre) {
        String valor = capitalizarPrimeraLetra(normalizar(nombre, "La transportadora es obligatoria"));
        return transportadoraRepository.findByNombreIgnoreCase(valor).orElseGet(() -> {
            Transportadora nueva = new Transportadora();
            nueva.setNombre(valor);
            return transportadoraRepository.save(nueva);
        });
    }

    private TipoMovimiento resolverMovimiento(String movimiento) {
        if (movimiento == null || movimiento.isBlank()) {
            throw new BadRequestException("El tipo de movimiento es obligatorio para transportadoras");
        }
        try {
            return TipoMovimiento.valueOf(movimiento.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("El tipo de movimiento debe ser IN u OUT");
        }
    }

    private TipoVisitante resolverTipo(String tipoRegistro) {
        List<String> nombres = switch (tipoRegistro.trim().toLowerCase(Locale.ROOT)) {
            case "general" -> List.of("Registro general", "Colaboradores", "Colaborador");
            case "proveedores" -> List.of("Proveedores", "Proveedor");
            case "transportadoras" -> List.of("Transportadoras", "Transportadora");
            case "taxis" -> List.of("Taxis", "Taxi");
            default -> throw new BadRequestException("Tipo de registro inválido");
        };
        return nombres.stream().map(tipoRepository::findByNombreIgnoreCase).flatMap(java.util.Optional::stream).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El tipo de visitante no está configurado"));
    }

    private EstatusVisita resolverEstatus() {
        return estatusRepository.findByNombreIgnoreCase("ACTIVO")
                .or(() -> estatusRepository.findByNombreIgnoreCase("EN SITIO"))
                .or(() -> estatusRepository.findByNombreIgnoreCase("PENDIENTE"))
                .orElseGet(() -> estatusRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("El estatus de visita no está configurado")));
    }

    private String normalizar(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) throw new BadRequestException(mensaje);
        return valor.trim();
    }

    private String blankToNull(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String capitalizarPrimeraLetra(String valor) {
        return Character.toUpperCase(valor.charAt(0)) + valor.substring(1);
    }
}