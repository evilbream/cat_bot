package com.baranova.cat_bot.telegram.constants;

public interface UserMessage {
    String COMMAND_START = "/start";

    String MESSAGE_START = "Это главное меню бота с котиками, здесь ты можешь посмотреть чужих котиков и добавить своих!";
    String MESSAGE_START_PROMPT = "Для старта напишите /start";
    String MESSAGE_MY_CATS = "Вот список ваших котиков. Для детальной информации воспользуйтесь кнопкой";
    String MESSAGE_NO_MY_CATS = "У вас еще нет котиков. Скорее добавьте их!";
    String MESSAGE_ADD_CAT_PHOTO = "Отправьте мне фото котика";
    String MESSAGE_CONFIRM_PHOTO = "Котик добавлен, сохранить?";
    String MESSAGE_ADD_CAT_NAME = "Отправьте мне имя котика";
    String MESSAGE_CONFIRM_NAME = "Имя выбрано, сохранить?";
    String MESSAGE_PHOTO_SAVED = "Котик Успешно сохранен!";
    String MESSAGE_PHOTO_NOT_SAVED = "Не удалось сохранить котика";
    String MESSAGE_REDO = "Начинаем заново!";
    String MESSAGE_NO_MORE_CATS = "Больше картинок не осталось";
    String MESSAGE_UNKNOWN = "Ты отправил что-то не то, попробуй еще раз или нажми /start для начала";
    String MESSAGE_UNKNOWN_PHOTO = "Зачем ты отправил мне фото? Попробуй еще раз или нажми /start для начала";
    String MESSAGE_CAT_DELETED = "Котик удален!";
    String MESSAGE_CAT_NOT_DELETED = "Не удалось удалить котика. Котик не найден";
    String MESSAGE_CAT_END_PAGE = "Это была последняя страница, больше котиков нет";

    String BUTTON_MY_CATS = "Мои котики";
    String BUTTON_VIEW_CATS = "Посмотреть котиков";
    String BUTTON_ADD_CAT_ASK_NAME = "Добавить котика";
    String BUTTON_NEXT = "Далее";
    String BUTTON_BACK = "Назад";
    String BUTTON_REDO = "Начать заново";
    String BUTTON_CONFIRM = "Подтвердить";
    String BUTTON_REMOVE = "Удалить";
    String BUTTON_LIKE = "👍";
    String BUTTON_DISLIKE = "👎";
    String BUTTON_TO_MAIN_MENU = "Главное меню";


    Integer MAX_PHOTOS_PER_PAGE = 3;


}