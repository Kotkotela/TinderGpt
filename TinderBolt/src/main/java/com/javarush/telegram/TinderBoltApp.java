package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "Rypert_2_0_bot";
    public static final String TELEGRAM_BOT_TOKEN = "7247249057:AAGTasdJulf_U_8kM4NJwzDg8MhP4nId1Vw";
    public static final String OPEN_AI_TOKEN = "gpt:y8HQgXidYWEPQ52jBuwnJFkblB3T8AScAHOTiNP46pG97Qae";
    private ChatGPTService chatGPT = new ChatGPTService((OPEN_AI_TOKEN));
    private DialogMode currendMode = null;
    private  UserInfo me;
    private  UserInfo she;
    private int questionCount;


    private  ArrayList<String> list = new ArrayList<>();

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();
        if (message.equals("/start")){
            currendMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);
            showMainMenu("главное меню бота","/start",
                    "генерация Tinder-профляT", "/profile",
                    "сообщение для знакомства", "/opener",
                    "переписка от вашего имени", "/message",
                    "переписка со звездами", "/date",
                    "общение с ChatGPT", "/gpt"
            );
            return;
        }



        if (message.equals("/gpt")){
            currendMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage("Чат gpt у аппарата :| ");
            return;
        }
        if (currendMode == DialogMode.GPT && !isMessageCommand()){
            String prompt = loadPrompt("gpt");
            Message nsg = sendTextMessage("хм, дайте подумать");
            String answer = chatGPT.sendMessage(prompt, message);
            updateTextMessage(nsg, answer);
            return;
        }




        if(message.equals("/date")){
            currendMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text,
                    "Ариана Гранде", "date_grande",
                    "Зендея", "date_zendaya",
                    "Марго Робби", "date_robbie",
                    "Райан Гослинг", "date_gosling",
                    "Том Харди", "date_hardy");
            return;
        }
        if (currendMode == DialogMode.DATE && !isMessageCommand()){
            String query = getCallbackQueryButtonKey();
            if(query.startsWith("date_")){
                sendPhotoMessage(query);
                sendTextMessage("Вперед к разврату шалунишка, твоя задача склонить её/его к интуму за 5 сообщений \uD83D\uDE08 \uD83D\uDE08 \uD83D\uDE08");


                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }
            Message nsg = sendTextMessage("хм, дайте подумать");
            String answer = chatGPT.addMessage(message);
            updateTextMessage(nsg, answer);
            return;
        }


        if (message.equals("/message")){
            currendMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Пришлите в чат вашу переписку",
                    "следующее сообщаение", "message_next",
                             "пригласить на свидание", "message_date");
            return;
        }
        if (currendMode == DialogMode.MESSAGE && !isMessageCommand()){
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")){
                String prompt = loadPrompt(query);
                String userChatHistory = String.join("\n\n", list);
                Message nsg = sendTextMessage("хм, дайте подумать");
                String answer = chatGPT.sendMessage(prompt,userChatHistory);
                updateTextMessage(nsg, answer);
                return;
            }

            list.add(message);
            return;
        }

        if(message.equals("/profile")){
            currendMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");

            me = new UserInfo();
            questionCount = 1;
            sendTextMessage("сколько вам лет?");
            return;
        }
        if(currendMode == DialogMode.PROFILE && !isMessageCommand()){
            switch (questionCount){
                case 1:
                    me.age = message;
                    questionCount=2;
                    sendTextMessage("кем вы работаете?");
                    return;
                case 2:
                    me.occupation = message;
                    questionCount = 3;
                    sendTextMessage("у вас есть хобби?");
                    return;
                case 3:
                    me.hobby = message;
                    questionCount = 4;
                    sendTextMessage("что вам Не нравится в людях?");
                    return;
                case 4:
                    me.annoys = message;
                    questionCount = 5;
                    sendTextMessage("цель знакомства?");
                    return;
                case 5:
                    me.goals = message;
                    String aboutMyself = me.toString();
                    String prompt =  loadPrompt("profile");
                    Message nsg = sendTextMessage("хм, дайте подумать");
                    String answer = chatGPT.sendMessage(prompt, aboutMyself);
                    updateTextMessage(nsg, answer);
                    return;

            }
                return;
        }
        if (message.equals("/opener")){
            currendMode = DialogMode.OPENER;
            sendPhotoMessage("opener");
            she = new UserInfo();
            questionCount = 1;
            sendTextMessage("имя девушки?");
            return;
        }
        if (currendMode == DialogMode.OPENER && !isMessageCommand()){
            switch (questionCount){
                case 1:
                    she.name = message;
                    questionCount = 2;
                    sendTextMessage("сколько ей лет?");
                    return;
                case 2:
                    she.age = message;
                    questionCount = 3;
                    sendTextMessage("есть ли у нее хобби?");
                    return;
                case 3:
                    she.hobby = message;
                    questionCount = 4;
                    sendTextMessage("кем она работает?");
                    return;
                case 4:
                    she.occupation = message;
                    questionCount = 5;
                    sendTextMessage("цель знакомства");
                    return;
                case 5:
                    she.goals = message;
                    String aboutFriend = message;
                    String prompt =  loadPrompt("opener");
                    Message nsg = sendTextMessage("хм, дайте подумать");
                    String answer = chatGPT.sendMessage(prompt, aboutFriend);
                    updateTextMessage(nsg, answer);
                    return;

            }
            return;
        }

        sendTextMessage("*привет*");
        sendTextMessage("_привет_");
        sendTextMessage("вы написали " + message);
        sendTextButtonsMessage("выберите режим работы:", "Старт", "Start", "Стоп", "Stop");
        sendPhotoMessage("avatar_main");

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
