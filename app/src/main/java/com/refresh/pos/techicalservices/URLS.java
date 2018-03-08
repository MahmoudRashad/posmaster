package com.refresh.pos.techicalservices;

/**
 * Created by mahmoudrashad on 2/11/2018.
 */

public class URLS {

    public static String Server_url="http://erp.wasltec.com/";
    public static String login=Server_url+"account/sign-in" ; //login api link
    public static String get_All_Items=Server_url+"api/forms/inventory/items/all";
    public static String Get_Branches=Server_url+"account/log-in/offices";
    public static String Submit_order=Server_url+"dashboard/sales/tasks/entry/new";
    public static String Get_Transactions=Server_url+"dashboard/sales/tasks/entry/search";
    public static String price_types=Server_url+"api/forms/sales/price-types/display-fields";
    public static String stores=Server_url+"api/forms/inventory/stores/display-fields";
    public static String counters=Server_url+"api/forms/inventory/counters/display-fields/get-where";
}



