package com.javarush.telegram;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;


import java.util.ArrayList;
import java.util.List;


public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "Rypert";
    public static final String TELEGRAM_BOT_TOKEN = "7247249057:AAGTasdJulf_U_8kM4NJwzDg8MhP4nId1Vw";

    private static final Long YOUR_TELEGRAM_ID = 1400914525L; // Ваш Telegram ID

    private DialogMode currendMode = null;
    private UserInfo me;
    private int questionCount;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();

            // Проверяем, если сообщение содержит фото
            if (update.getMessage().hasPhoto()) {
                if (currendMode == DialogMode.ZAKAZ && questionCount == 3) {
                    String photoId = update.getMessage().getPhoto().get(0).getFileId();
                    me.photoFileId = photoId;

                    // Проверяем, есть ли подпись к фото
                    if (update.getMessage().getCaption() != null) {
                        me.print = update.getMessage().getCaption(); // сохраняем подпись к фото
                    }

                    sendTextMessage("Фото успешно загружено! Теперь укажите свои контакты:");
                    questionCount = 4; // Переход к следующему вопросу
                    return;
                }
            }

            // Обработка текстовых сообщений
            String message = update.getMessage().getText();

            if (message.equals("/start")) {
                currendMode = DialogMode.MAIN;
                sendPhotoMessage("main");
                String text = loadMessage("main");
                sendTextMessage(text);
                showMainMenu("Главное меню бота", "/start",
                        "Сделать заказ", "/zakaz",
                        "Уточнить информацию", "/info",
                        "Просмотреть готовые работы", "/gotovoe");
                return;
            }

            if (message.equals("/zakaz")) {
                currendMode = DialogMode.ZAKAZ;
                sendPhotoMessage("zakaz");

                me = new UserInfo();
                questionCount = 1;
                sendTextMessage("На какой вид одежды вы хотите нанести принт?");
                return;
            }

            // Обработка текстовых сообщений в режиме заказа
            if (currendMode == DialogMode.ZAKAZ) {
                switch (questionCount) {
                    case 1:
                        me.vit = message;
                        questionCount = 2;
                        sendTextMessage("Укажите свой размер?");
                        return;
                    case 2:
                        me.razmer = message;
                        questionCount = 3;
                        sendTextMessage("Опишите принт, его расположение и примерные размеры, также вы можете отправить фото.");
                        return;
                    case 3:
                        me.print = message;
                        questionCount = 4;
                        sendTextMessage("Контакты, чтобы с вами связаться:");
                        return;
                    case 4:
                        me.kontakt = message;
                        sendTextMessage("Спасибо, в скором времени свяжемся с вами!)");
                        sendReportToSelf(me);
                        resetVariables();
                        return;
                }
            }

            if (message.equals("/info")) {
                currendMode = DialogMode.INFO;
                sendPhotoMessage("akril");
                me = new UserInfo();
                questionCount = 1;
                sendTextMessage("Напишите свой вопрос");
                return;
            }

            if (currendMode == DialogMode.INFO) {
                switch (questionCount) {
                    case 1:
                        questionCount = 2;
                        me.vopros = message;
                        sendTextMessage("Напишите свои контакты, чтобы наш специалист мог вам ответить");
                        return;
                    case 2:
                        questionCount = 3;
                        me.kontakt = message;
                        sendTextMessage("Спасибо, скоро с вами свяжемся!");
                        sendReportToSelf2(me);
                        resetVariables();
                        return;
                }
            }

            if (message.equals("/gotovoe")) {
                currendMode = DialogMode.GOTOVOE;
                sendTextMessage("Взгляните на уже готовые работы, если вам что-то понравится, то можете оформить заказ");
                sendPhotoMessage("zakaz");
                sendPhotoMessage("main");
                me = new UserInfo();

                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> buttons = new ArrayList<>();
                KeyboardRow row = new KeyboardRow();
                row.add(new KeyboardButton("Лиса"));
                row.add(new KeyboardButton("Котики"));
                buttons.add(row);
                replyMarkup.setKeyboard(buttons);
                replyMarkup.setResizeKeyboard(true);

                sendTextMessageWithKeyboard("Выберите понравившееся:", replyMarkup, chatId);
                return;
            }

            if (currendMode == DialogMode.GOTOVOE) {
                if (message.equals("Лиса") || message.equals("Котики")) {
                    me.vit = message;
                    questionCount = 2;
                    sendTextMessage("Укажите свой размер?");
                    return;
                }

                switch (questionCount) {
                    case 2:
                        me.razmer = message;
                        questionCount = 3;
                        sendTextMessage("Контакты, чтобы с вами связаться:");
                        return;
                    case 3:
                        me.kontakt = message;
                        sendTextMessage("Спасибо, в скором времени свяжемся с вами!)");
                        sendReportToSelf3(me);
                        resetVariables();
                        return;
                }
            }
        }
    }

    private void resetVariables() {
        me = null; // Убираем объект UserInfo
        currendMode = null; // Сбрасываем текущий режим
        questionCount = 0; // Сбрасываем счетчик вопросов
    }

    private void sendReportToSelf(UserInfo userInfo) {
        String report = "Новый заказ:\n" +
                "Вид одежды: " + userInfo.vit + "\n" +
                "Размер: " + userInfo.razmer + "\n" +
                "Принт: " + userInfo.print + "\n" +
                "Контакты: " + userInfo.kontakt;

        sendTextMessageToUser(report, YOUR_TELEGRAM_ID);

        if (userInfo.photoFileId != null) {
            try {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(YOUR_TELEGRAM_ID.toString());
                sendPhoto.setPhoto(new InputFile(userInfo.photoFileId));
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendReportToSelf2(UserInfo userInfo) {
        String report = "Поступил вопрос:\n" +
                "Вопрос: " + userInfo.vopros + "\n" +
                "Контакты: " + userInfo.kontakt;

        sendTextMessageToUser(report, YOUR_TELEGRAM_ID);
    }

    private void sendReportToSelf3(UserInfo userInfo) {
        String report = "Поступил заказ на готовую работу:\n" +
                "Вид: " + userInfo.vit + "\n" +
                "Размер: " + userInfo.razmer + "\n" +
                "Контакты: " + userInfo.kontakt;

        sendTextMessageToUser(report, YOUR_TELEGRAM_ID);
    }

    private void sendTextMessageToUser(String text, Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message); // Отправить сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Обработка исключений
        }
    }

    private void sendTextMessageWithKeyboard(String text, ReplyKeyboardMarkup replyMarkup, Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(replyMarkup);

        try {
            execute(message); // Отправить сообщение
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Обработка исключений
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
