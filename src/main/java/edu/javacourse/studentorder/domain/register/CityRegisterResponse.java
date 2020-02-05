package edu.javacourse.studentorder.domain.register;

/** Это простой ответ ГРН на запрос об ОДНОЙ персоне. */
public class CityRegisterResponse {
    private boolean isExist;
    private Boolean isTemporal;

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public Boolean getTemporal() {
        return isTemporal;
    }

    public void setTemporal(Boolean temporal) {
        isTemporal = temporal;
    }

    @Override
    public String toString() {
        return "CityRegisterCheckerResponse{" +
                "isExist=" + isExist +
                ", isTemporal=" + isTemporal +
                '}';
    }
}
