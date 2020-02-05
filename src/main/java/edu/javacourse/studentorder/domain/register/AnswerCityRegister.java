package edu.javacourse.studentorder.domain.register;

import java.util.ArrayList;
import java.util.List;

/** Хранит результаты проверки ВСЕХ персон в заявлении */
public class AnswerCityRegister {
    private List<AnswerCityRegisterItem> items;

    public void addItem(AnswerCityRegisterItem item){
        if (this.items == null){
            this.items = new ArrayList<>(10);
        }
        this.items.add(item);
    }

    public List<AnswerCityRegisterItem> getItems() {
        return items;
    }
}
