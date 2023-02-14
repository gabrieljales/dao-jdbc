package application;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.Scanner;

public class Program2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

        System.out.println("=== TEST 1: department insert ===");
        Department newDepartment = new Department(null, "Finance");
        departmentDao.insert(newDepartment);
        System.out.println("Department inserted! New id = " + newDepartment.getId());

        scanner.close();
    }
}
