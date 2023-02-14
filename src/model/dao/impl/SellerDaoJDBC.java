package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJDBC implements SellerDao {

    private Connection connection; // Dependência da conexão do banco

    // Forçando a injeção de dependência aqui
    public SellerDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Seller obj) {

    }

    @Override
    public void update(Seller obj) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.DepartmentId = department.Id "
                    + "WHERE seller.Id = ?"
            );

            statement.setInt(1, id);
            // Resultado do resultSet vêm apontando para a posição 0, que não contém objeto! Por isso o if abaixo
            resultSet = statement.executeQuery();

            // Retornou um vendedor
            if (resultSet.next()) {
                // ResultSet trás os dados em formato de tabela, um objeto com linhas e colunas!
                // Mas como estamos programando OO, a nossa classe DAO é responsável por pegar os dados do banco relacional
                // e transformar em objetos associados, como foi feito abaixo! Criado um departamento e depois associado à um vendedor
                Department department = instantiateDepartment(resultSet);
                Seller seller = instantiateSeller(resultSet, department);

                return seller;
            }

            return  null; // Não foi encontrado nada
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(statement);
            DB.closeResultSet(resultSet);
        }
    }

    // OBS: porque não tratar a exceção com try/catch aqui? Porque nos métodos acima já é feito isso
    // Portanto, nesse método auxiliar nós vamos simplesmente PROPAGAR a exceção, colocanco na assinatura do método!
    private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
        Seller seller = new Seller();
        seller.setId(resultSet.getInt("Id"));
        seller.setName(resultSet.getString("Name"));
        seller.setEmail(resultSet.getString("Email"));
        seller.setBaseSalary(resultSet.getDouble("BaseSalary"));
        seller.setBirthDate(resultSet.getDate("BirthDate"));
        seller.setDepartment(department);

        return  seller;
    }

    private Department instantiateDepartment(ResultSet resultSet) throws SQLException{
        Department department = new Department();
        department.setId(resultSet.getInt("DepartmentId"));
        department.setName(resultSet.getString("DepName"));

        return department;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                            + "FROM seller INNER JOIN department "
                            + "ON seller.DepartmentId = department.Id "
                            + "ORDER BY Name"
            );

            resultSet = statement.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>(); // map vazio para guardar departamentos instanciados

            // Como podem ser retornados mais do quê um objeto, usamos o while invés do if
            // Lembrando que o correto é que vários vendedores apontem para o mesmo departamento, por isso esse cuidado
            while (resultSet.next()) {
                // Antes de instanciar o departamento, testamos se le já existe!
                Department dep = map.get(resultSet.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    map.put(resultSet.getInt("DepartmentId"), dep); // Salvando departamento dentro do map para verificar em uma proxima vez se ele existe
                }

                Seller seller = instantiateSeller(resultSet, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(statement);
            DB.closeResultSet(resultSet);
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(
                    "SELECT seller.*, department.Name as DepName "
                    + "FROM seller INNER JOIN department "
                    + "ON seller.DepartmentId = department.Id "
                    + "WHERE DepartmentId = ? "
                    + "ORDER BY Name"
            );

            statement.setInt(1, department.getId());
            resultSet = statement.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>(); // map vazio para guardar departamentos instanciados

            // Como podem ser retornados mais do quê um objeto, usamos o while invés do if
            // Lembrando que o correto é que vários vendedores apontem para o mesmo departamento, por isso esse cuidado
            while (resultSet.next()) {
                // Antes de instanciar o departamento, testamos se le já existe!
                Department dep = map.get(resultSet.getInt("DepartmentId"));

                if (dep == null) {
                    dep = instantiateDepartment(resultSet);
                    map.put(resultSet.getInt("DepartmentId"), dep); // Salvando departamento dentro do map para verificar em uma proxima vez se ele existe
                }

                Seller seller = instantiateSeller(resultSet, dep);
                list.add(seller);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(statement);
            DB.closeResultSet(resultSet);
        }
    }
}
