package com.baranova.tg_service.constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import com.baranova.shared.constants.UserMessage;

public interface MessageCallback {
    String VIEW_CATS = "view_cats";
    String ADD_CAT_ASK_NAME = "add_cat_ask_name";
    String MY_CATS = "my_cats";
    String BACK = "back";
    String NEXT_PAGE = "next_page";
    String REDO = "redo";
    String CONFIRM = "confirm";
    String PREVIOUS_PAGE = "prev_page";
    String REMOVE = "remove";
    String MENU = "menu";
    String ADD_PHOTO = "add_photo";
    String EXECUTE_PHOTO = "execute_photo";
    String RESTART_VIEVING = "restart_viewing";

    ArrayList<String> ALLOWED_CALLBACKS = new ArrayList<String>() {{
        add(NEXT_PAGE);
        add(PREVIOUS_PAGE);
        add(MENU);
        add(BACK);
        add(CONFIRM);
        add(REDO);
    }};

    Integer DEFAULT_BUTTONS_PER_ROW = 3;

    Map<String, String> START_BUTTONS = new LinkedHashMap<>() {{
        put(UserMessage.BUTTON_VIEW_CATS, MessageCallback.VIEW_CATS);
        put(UserMessage.BUTTON_ADD_CAT_ASK_NAME, MessageCallback.ADD_CAT_ASK_NAME);
        put(UserMessage.BUTTON_MY_CATS, MessageCallback.MY_CATS);
    }};

}
