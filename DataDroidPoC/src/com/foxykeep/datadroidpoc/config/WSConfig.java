/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.config;

public class WSConfig {

    public static final String ROOT_URL = "http://foxykeep.com/projects/datadroid/";

    // PersonList WS
    public static final String WS_PERSON_LIST_URL_XML = ROOT_URL + "personListXml.php";
    public static final String WS_PERSON_LIST_URL_JSON = ROOT_URL + "personListJson.php";

    // CityList WS
    public static final String WS_CITY_LIST_URL = ROOT_URL + "cityListJson.php";

    // CrudPhoneList WS
    public static final String WS_CRUD_PHONE_LIST_URL = ROOT_URL + "crud/list.php";

    public static final String WS_CRUD_PHONE_LIST_PROPERTY_USER_ID = "userId";
}
