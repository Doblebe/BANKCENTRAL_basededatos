package bankcentral.repository;

import bankcentral.domain.Cliente;
import bankcentral.enums.TipoCuenta;
import bankcentral.service.outputport.ClientePersistencePort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository implements ClientePersistencePort {

    // Lista que funciona como nuestra base de datos en memoria
    private final List<Cliente> clientes = new ArrayList<>();

    // Datos iniciales
    public ClienteRepository() {
        clientes.add(new Cliente(1, "1001234567", "Carlos",  "Lopez",    "3001234567", "carlos", "1234", TipoCuenta.AHORROS));
        clientes.add(new Cliente(2, "1007654321", "Maria",   "Gonzalez", "3107654321", "maria",  "5678", TipoCuenta.CORRIENTE));
        clientes.add(new Cliente(3, "1009876543", "Andres",  "Perez",    "3209876543", "andres", "9012", TipoCuenta.CREDITO));

        // Saldos iniciales de ejemplo
        clientes.get(0).getCuenta().consignar(2_000_000);
        clientes.get(1).getCuenta().consignar(1_500_000);
    }

    // CREATE
    @Override
    public Cliente saveCliente(Cliente cliente) {
        clientes.add(cliente);
        System.out.println("Cliente creado: " + cliente.getNombre() + " " + cliente.getApellido());
        return cliente;
    }

    // READ - por id
    @Override
    public Optional<Cliente> findClienteById(int id) {
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }

    // READ - por usuario
    @Override
    public Optional<Cliente> findClienteByUsuario(String usuario) {
        for (Cliente cliente : clientes) {
            if (cliente.getUsuario().equalsIgnoreCase(usuario)) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty();
    }

    // READ - todos
    @Override
    public List<Cliente> findAllClientes() {
        for (Cliente cliente : clientes) {
            System.out.println(cliente.toString());
        }
        return clientes;
    }

    // UPDATE - devuelve el objeto para que el service lo modifique
    @Override
    public Cliente updateCliente(Cliente cliente) {
        for (Cliente c : clientes) {
            if (c.getId() == cliente.getId()) {
                return c;
            }
        }
        return null;
    }

    // DELETE
    @Override
    public void deleteCliente(int id) {
        Cliente toDelete = null;
        for (Cliente cliente : clientes) {
            if (cliente.getId() == id) {
                toDelete = cliente;
                break;
            }
        }
        if (toDelete != null) {
            clientes.remove(toDelete);
            System.out.println("Cliente eliminado correctamente.");
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }
}
