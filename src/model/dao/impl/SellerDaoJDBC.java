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
import java.util.List;

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
        return null;
    }
}
