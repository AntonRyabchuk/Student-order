package edu.javacourse.studentorder;

import edu.javacourse.studentorder.dao.DictionaryDaoImpl;
import edu.javacourse.studentorder.dao.IStudentOrderDao;
import edu.javacourse.studentorder.dao.StudentDaoImpl;
import edu.javacourse.studentorder.domain.*;
import edu.javacourse.studentorder.exception.DaoException;

import java.time.LocalDate;
import java.util.List;

public class SaveStudentOrder {
    public static void main(String[] args) throws DaoException {
//        List<Street> streetList = new DictionaryDaoImpl().findStreets("Про");
//        for (Street street : streetList){
//            System.out.println(street.getStreetName());
//        }
//
//        List<PassportOffice> passportOffices = new DictionaryDaoImpl().findPassportOffices("010020000000");
//        for (PassportOffice po : passportOffices){
//            System.out.println(po.getOfficeName());
//        }
//
//        List<RegisterOffice> registerOffices = new DictionaryDaoImpl().findRegisterOffices("010010000000");
//        for (RegisterOffice ro : registerOffices){
//            System.out.println(ro.getOfficeName());
//        }

//        List<CountryArea> countryAreas1 = new DictionaryDaoImpl().findArea("");
//        for (CountryArea ca : countryAreas1){
//            System.out.println(ca.getAreaId() + " : " + ca.getAreaName());
//        }
//        System.out.println();
//        List<CountryArea> countryAreas2 = new DictionaryDaoImpl().findArea("020000000000");
//        for (CountryArea ca : countryAreas2){
//            System.out.println(ca.getAreaId() + " : " + ca.getAreaName());
//        }
//        System.out.println();
//        List<CountryArea> countryAreas3 = new DictionaryDaoImpl().findArea("020020000000");
//        for (CountryArea ca : countryAreas3){
//            System.out.println(ca.getAreaId() + " : " + ca.getAreaName());
//        }
//        System.out.println();
//        List<CountryArea> countryAreas4 = new DictionaryDaoImpl().findArea("020020020000");
//        for (CountryArea ca : countryAreas4){
//            System.out.println(ca.getAreaId() + " : " + ca.getAreaName());
//        }

//        StudentOrder studentOrder = buildStudentOrder(10);
        IStudentOrderDao studentOrderDao = new StudentDaoImpl();
//        Long orderId = studentOrderDao.saveStudentOrder(studentOrder);
//        System.out.println(orderId);

        List<StudentOrder> studentOrders = studentOrderDao.getStudentOrders();
//        studentOrders.forEach(s -> System.out.println(s.getStudentOrderId()));
        for(StudentOrder so : studentOrders){
            System.out.print (so.getStudentOrderId() + " : ");
            System.out.println(so.getHusband().getGivenName());
        }

        System.out.println();
    }

    static long saveStudentOrder(StudentOrder studentOrder){
        long answer = 199;
        return answer;
    }

    public static StudentOrder buildStudentOrder(long id) {
        StudentOrder studentOrder = new StudentOrder();
        studentOrder.setStudentOrderId(id);
        studentOrder.setMarriageCertificateId("" + (123456000 + id));
        studentOrder.setMarriageDate(LocalDate.of(2016, 7, 25));
        RegisterOffice registerOffice = new RegisterOffice(1L, "", "");
        studentOrder.setMarriageOffice(registerOffice);

        Street street = new Street(1L, "First street");

        Address address = new Address("195000", street, "12", "", "142");

        //Муж
        Adult husband = new Adult("Петров", "Виктор", "Сергеевич", LocalDate.of(1997, 7, 24));
        husband.setPassportSeria("" + (1000 + id));
        husband.setPassportNumber("" + (100000 + id));
        husband.setIssueDate(LocalDate.of(2017,9,15));
        PassportOffice passportOffice1 = new PassportOffice(1L, "", "");
        husband.setIssueDepartment(passportOffice1);
        husband.setAddress(address);
        husband.setUniversity(new University(2L, ""));
        husband.setStudentId("HH12345");

        //Жена
        Adult wife = new Adult("Петрова", "Вероника", "Алексеевна", LocalDate.of(1998, 3, 12));
        wife.setPassportSeria("" + (2000 + id));
        wife.setPassportNumber("" + (200000 + id));
        wife.setIssueDate(LocalDate.of(2018,3,13));
        PassportOffice passportOffice2 = new PassportOffice(2L, "", "");
        wife.setIssueDepartment(passportOffice2);
        wife.setAddress(address);
        wife.setUniversity(new University(1L, ""));
        wife.setStudentId("WW12345");

        //Ребенок
        Child child = new Child("Петрова", "Ирина", "Викторовна", LocalDate.of(2019, 12, 31));
        child.setCertificateNumber("" + (300000 + id));
        child.setIssueDate(LocalDate.of(2020, 1, 1));
        RegisterOffice registerOffice2 = new RegisterOffice(2L, "", "");
        child.setIssueDepartment(registerOffice2);
        child.setAddress(address);

        //Ребенок2
        Child child2 = new Child("Петрова", "Марина", "Викторовна", LocalDate.of(2019, 12, 31));
        child2.setCertificateNumber("" + (400000 + id));
        child2.setIssueDate(LocalDate.of(2020, 1, 1));
        child2.setIssueDepartment(registerOffice2);
        child2.setAddress(address);

        studentOrder.setHusband(husband);
        studentOrder.setWife(wife);
        studentOrder.addChild(child);
        studentOrder.addChild(child2);

        return studentOrder;
    }
}
