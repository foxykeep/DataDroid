/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.config;

public final class JSONTag {

    private JSONTag() {
        // No public constructor
    }

    // PersonList WS tags
    public static final String PERSON_LIST_ELEM_PERSONS = "persons";
    public static final String PERSON_LIST_ELEM_PERSON = "person";
    public static final String PERSON_LIST_ELEM_PERSON_FIRST_NAME = "firstName";
    public static final String PERSON_LIST_ELEM_PERSON_LAST_NAME = "lastName";
    public static final String PERSON_LIST_ELEM_PERSON_EMAIL = "email";
    public static final String PERSON_LIST_ELEM_PERSON_CITY = "city";
    public static final String PERSON_LIST_ELEM_PERSON_POSTAL_CODE = "postalCode";
    public static final String PERSON_LIST_ELEM_PERSON_AGE = "age";
    public static final String PERSON_LIST_ELEM_PERSON_IS_WORKING = "isWorking";

    // CityList WS tags
    public static final String CITY_LIST_ELEM_CITIES = "cities";
    public static final String CITY_LIST_ELEM_CITY = "city";
    public static final String CITY_LIST_ELEM_CITY_NAME = "name";
    public static final String CITY_LIST_ELEM_CITY_POSTAL_CODE = "postalCode";
    public static final String CITY_LIST_ELEM_CITY_STATE = "state";
    public static final String CITY_LIST_ELEM_CITY_COUNTRY = "country";

    // CrudPhoneList WS tags
    public static final String CRUD_PHONE_LIST_ELEM_PHONES = "phones";
    public static final String CRUD_PHONE_LIST_ELEM_PHONE = "phone";
    public static final String CRUD_PHONE_LIST_ELEM_ID = "id";
    public static final String CRUD_PHONE_LIST_ELEM_NAME = "name";
    public static final String CRUD_PHONE_LIST_ELEM_MANUFACTURER = "manufacturer";
    public static final String CRUD_PHONE_LIST_ELEM_ANDROID_VERSION = "androidVersion";
    public static final String CRUD_PHONE_LIST_ELEM_SCREEN_SIZE = "screenSize";
    public static final String CRUD_PHONE_LIST_ELEM_PRICE = "price";

    // CrudPhoneDelete WS tags
    public static final String CRUD_PHONE_DELETE_ELEM_PHONES = "phones";
    public static final String CRUD_PHONE_DELETE_ELEM_PHONE = "phone";
    public static final String CRUD_PHONE_DELETE_ELEM_ID = "id";

    // CrudPhoneAddEdit WS tags
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_PHONE = "phone";
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_ID = "id";
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_NAME = "name";
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_MANUFACTURER = "manufacturer";
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_ANDROID_VERSION = "androidVersion";
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_SCREEN_SIZE = "screenSize";
    public static final String CRUD_PHONE_ADD_EDIT_ELEM_PRICE = "price";

    // RequestTypes WS tags
    public static final String REQUEST_TYPE_VALUE = "value";
}
