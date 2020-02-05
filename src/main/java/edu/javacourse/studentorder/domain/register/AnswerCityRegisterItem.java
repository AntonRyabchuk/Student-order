package edu.javacourse.studentorder.domain.register;

import edu.javacourse.studentorder.domain.Person;

/** Хранит результаты проверки ОДНОЙ персоны в заявлении */
public class AnswerCityRegisterItem {

    public enum CityStatus {
        YES, NO, ERROR
    }

    public static class CityError{
        private String code;
        private String text;

        public CityError(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    private CityStatus cityStatus;
    private Person person;
    private CityError error;

    public AnswerCityRegisterItem(CityStatus cityStatus, Person person) {
        this.cityStatus = cityStatus;
        this.person = person;
    }

    public AnswerCityRegisterItem(CityStatus cityStatus, Person person, CityError error) {
        this.cityStatus = cityStatus;
        this.person = person;
        this.error = error;
    }
}
