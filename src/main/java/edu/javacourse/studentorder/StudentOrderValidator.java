package edu.javacourse.studentorder;

import edu.javacourse.studentorder.domain.*;
import edu.javacourse.studentorder.domain.children.AnswerChildren;
import edu.javacourse.studentorder.domain.register.AnswerCityRegister;
import edu.javacourse.studentorder.domain.student.AnswerStudent;
import edu.javacourse.studentorder.domain.wedding.AnswerWedding;
import edu.javacourse.studentorder.mail.MailSender;
import edu.javacourse.studentorder.validator.ChildrenValidator;
import edu.javacourse.studentorder.validator.CityRegisterValidator;
import edu.javacourse.studentorder.validator.StudentValidator;
import edu.javacourse.studentorder.validator.WeddingValidator;

import java.util.LinkedList;
import java.util.List;

public class StudentOrderValidator {

    private CityRegisterValidator cityRegisterVal;
    private WeddingValidator weddingVal;
    private ChildrenValidator childrenVal;
    private StudentValidator studentVal;
    private MailSender mailSender;

    public StudentOrderValidator() {
        this.cityRegisterVal = new CityRegisterValidator();
        this.weddingVal = new WeddingValidator();
        this.childrenVal = new ChildrenValidator();
        this.studentVal = new StudentValidator();
        this.mailSender = new MailSender();
    }

    public static void main(String[] args) {
        StudentOrderValidator sov = new StudentOrderValidator();
        sov.checkAll();
    }

    public void checkAll() {
        List<StudentOrder> studentOrders = readStudentOrders();

        for (int i = 0; i < studentOrders.size(); i++){
            checkOneOrder(studentOrders.get(i));
        }

    }

    public void checkOneOrder(StudentOrder studentOrder){
        AnswerCityRegister answerCityRegister = checkCityRegister(studentOrder);
//        AnswerWedding answerWedding = checkWedding(studentOrder);
//        AnswerChildren answerChildren = checkChildren(studentOrder);
//        AnswerStudent answerStudent = checkStudent(studentOrder);
//
//        sendMail(studentOrder);
    }

    static List<StudentOrder> readStudentOrders() {
        List<StudentOrder> studentOrders  = new LinkedList<>();

        for(int i =0; i < 5; i++){
            studentOrders.add(SaveStudentOrder.buildStudentOrder(i));
        }
        return studentOrders;
    }

    public AnswerCityRegister checkCityRegister(StudentOrder studentOrder) {
        return this.cityRegisterVal.checkCityRegister(studentOrder);
    }

    public AnswerWedding checkWedding(StudentOrder studentOrder) {
        return this.weddingVal.checkWedding(studentOrder);
    }

    public AnswerChildren checkChildren(StudentOrder studentOrder) {
        ChildrenValidator cv = new ChildrenValidator();
        return this.childrenVal.checkChildren(studentOrder);
    }

    public AnswerStudent checkStudent(StudentOrder studentOrder) {
        StudentValidator sv = new StudentValidator();
        return sv.checkStudent(studentOrder);
    }

    public void sendMail(StudentOrder studentOrder) {
        this.mailSender.sendMail(studentOrder);
    }

}
