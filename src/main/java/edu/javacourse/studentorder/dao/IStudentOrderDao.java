package edu.javacourse.studentorder.dao;

import edu.javacourse.studentorder.domain.StudentOrder;
import edu.javacourse.studentorder.exception.DaoException;

import java.util.List;

public interface IStudentOrderDao {
    Long saveStudentOrder(StudentOrder studentOrder) throws DaoException;
    List<StudentOrder> getStudentOrders() throws DaoException;
}
