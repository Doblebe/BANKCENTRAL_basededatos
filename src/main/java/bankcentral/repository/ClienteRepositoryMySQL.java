package bankcentral.repository;

import bankcentral.config.ConexionDB;
import bankcentral.domain.*;
import bankcentral.enums.TipoCuenta;
import bankcentral.enums.TipoEstado;
import bankcentral.service.outputport.ClientePersistencePort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryMySQL implements ClientePersistencePort {

    private final Connection con;

    public ClienteRepositoryMySQL() throws SQLException {
        this.con = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public Cliente saveCliente(Cliente cliente) {
        String sqlCuenta = "INSERT INTO cuenta (id_tipo_cuenta, saldo, cupo) VALUES (?, ?, ?)";
        try (PreparedStatement psCuenta = con.prepareStatement(sqlCuenta, Statement.RETURN_GENERATED_KEYS)) {
            TipoCuenta tipo = cliente.getCuenta().getTipo();
            psCuenta.setInt(1, tipoCuentaToId(tipo));
            psCuenta.setDouble(2, cliente.getCuenta().getSaldo());
            if (tipo == TipoCuenta.CREDITO) {
                psCuenta.setDouble(3, ((TarjetaCredito) cliente.getCuenta()).getCupo());
            } else {
                psCuenta.setNull(3, Types.DECIMAL);
            }
            psCuenta.executeUpdate();
            ResultSet rs = psCuenta.getGeneratedKeys();
            int idCuenta = 0;
            if (rs.next()) idCuenta = rs.getInt(1);

            String sqlCliente = "INSERT INTO cliente "
                    + "(identificacion, nombre, apellido, celular, usuario, contrasena, id_estado, id_cuenta) "
                    + "VALUES (?, ?, ?, ?, ?, SHA2(?, 256), ?, ?)";
            try (PreparedStatement psCliente = con.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                psCliente.setString(1, cliente.getIdentificacion());
                psCliente.setString(2, cliente.getNombre());
                psCliente.setString(3, cliente.getApellido());
                psCliente.setString(4, cliente.getCelular());
                psCliente.setString(5, cliente.getUsuario());
                psCliente.setString(6, cliente.getContrasena());
                psCliente.setInt   (7, tipoEstadoToId(cliente.getEstado()));
                psCliente.setInt   (8, idCuenta);
                psCliente.executeUpdate();
                ResultSet rsC = psCliente.getGeneratedKeys();
                if (rsC.next()) cliente.setId(rsC.getInt(1));
            }
            System.out.println("✅ Cliente guardado: " + cliente.getNombre());
            return cliente;
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar cliente: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Cliente> findAllClientes() {
        List<Cliente> lista = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM vista_cliente_detalle")) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al listar: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Optional<Cliente> findClienteById(int id) {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM vista_cliente_detalle WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapearCliente(rs));
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findClienteByUsuario(String usuario) {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM vista_cliente_detalle WHERE usuario = ?")) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Cliente c = mapearCliente(rs);
                try (PreparedStatement ps2 = con.prepareStatement(
                        "SELECT contrasena FROM cliente WHERE usuario = ?")) {
                    ps2.setString(1, usuario);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) c.setContrasena(rs2.getString("contrasena"));
                }
                return Optional.of(c);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Cliente updateCliente(Cliente cliente) {
        try {
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE cliente SET nombre=?, apellido=?, celular=?, id_estado=?, intentos_fallidos=? WHERE id=?")) {
                ps.setString(1, cliente.getNombre());
                ps.setString(2, cliente.getApellido());
                ps.setString(3, cliente.getCelular());
                ps.setInt   (4, tipoEstadoToId(cliente.getEstado()));
                ps.setInt   (5, cliente.getIntentosFallidos());
                ps.setInt   (6, cliente.getId());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE cuenta cu JOIN cliente cl ON cl.id_cuenta = cu.id SET cu.saldo=? WHERE cl.id=?")) {
                ps.setDouble(1, cliente.getCuenta().getSaldo());
                ps.setInt   (2, cliente.getId());
                ps.executeUpdate();
            }
            return cliente;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteCliente(int id) {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas > 0) System.out.println("✅ Cliente eliminado.");
            else           System.out.println("⚠️ Cliente no encontrado.");
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId             (rs.getInt   ("id"));
        c.setIdentificacion (rs.getString("identificacion"));
        c.setNombre         (rs.getString("nombre"));
        c.setApellido       (rs.getString("apellido"));
        c.setCelular        (rs.getString("celular"));
        c.setUsuario        (rs.getString("usuario"));
        c.setContrasena     ("**protegida**");
        c.setEstado         (TipoEstado.valueOf(rs.getString("estado")));
        double saldo = rs.getDouble("saldo");
        Cuenta cuenta;
        switch (TipoCuenta.valueOf(rs.getString("tipo_cuenta"))) {
            case AHORROS:
                cuenta = new CuentaAhorros();
                cuenta.setSaldo(saldo);
                break;
            case CORRIENTE:
                cuenta = new CuentaCorriente();
                cuenta.setSaldo(saldo);
                break;
            default:
                cuenta = new TarjetaCredito(rs.getDouble("cupo"));
                cuenta.setSaldo(saldo);
                break;
        }
        c.setCuenta(cuenta);
        return c;
    }

    private int tipoCuentaToId(TipoCuenta tipo) {
        return switch (tipo) {
            case AHORROS   -> 1;
            case CORRIENTE -> 2;
            case CREDITO   -> 3;
        };
    }

    private int tipoEstadoToId(TipoEstado estado) {
        return switch (estado) {
            case ACTIVO    -> 1;
            case BLOQUEADO -> 2;
        };
    }
}