package com.baranova.cat_service.constants;

import java.util.ArrayList;
import java.util.Map;

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

    ArrayList<String> ALLOWED_CALLBACKS = new ArrayList<String>() {{
        add(NEXT_PAGE);
        add(PREVIOUS_PAGE);
        add(MENU);
        add(BACK);
        add(CONFIRM);
        add(REDO);
    }};

    int DEFAULT_BUTTONS_PER_ROW = 3;

    Map<String, String> START_BUTTONS = Map.of(UserMessage.BUTTON_VIEW_CATS, MessageCallback.VIEW_CATS,
            UserMessage.BUTTON_ADD_CAT_ASK_NAME, MessageCallback.ADD_CAT_ASK_NAME,
            UserMessage.BUTTON_MY_CATS, MessageCallback.MY_CATS
    );

}
