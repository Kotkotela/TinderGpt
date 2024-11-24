package com.javarush.telegram;
class UserInfo {
    public String kontakt; // Контакты
    public String vit; // Вид одежды
    public String razmer; // Размер одежды
    public String adres; // Адрес доставки
    public String vopros; // Вопрос клиента
    public String occupation; // Профессия
    public String printDescription; // Поле для сохранения описания принта
    public String handsome; // Привлекательность
    public String wealth; // Доход
    public String annoys; // Раздражения
    public String print; // Принт
    public String photoFileId; // ID фото, если оно было отправлено

    private String fieldToString(String str, String description) {
        if (str != null && !str.isEmpty())
            return description + ": " + str + "\n";
        else
            return "";
    }

    @Override
    public String toString() {
        String result = "";

        result += fieldToString(kontakt, "контакт для связи");
        result += fieldToString(vit, "вид одежды");
        result += fieldToString(razmer, "размер одежды");
        result += fieldToString(adres, "адрес доставки");
        result += fieldToString(vopros, "вопрос клиента");
        result += fieldToString(occupation, "Профессия");
        result += fieldToString(printDescription, "Поле для сохранения описания принта");
        result += fieldToString(handsome, "Красота, привлекательность в баллах (максимум 10 баллов)");
        result += fieldToString(wealth, "Доход, богатство");
        result += fieldToString(annoys, "В людях раздражает");
        result += fieldToString(print, "изображение либо описание принта");

        return result;
    }
}
