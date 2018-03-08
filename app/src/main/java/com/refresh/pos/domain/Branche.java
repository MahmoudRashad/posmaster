package com.refresh.pos.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mahmoudrashad on 2/12/2018.
 */

public class Branche {
    String OfficeId;
    String OfficeName;

    public Branche(String officeId, String officeName) {
        OfficeId = officeId;
        OfficeName = officeName;
    }

    public Branche(JSONObject row) {
        try {this.OfficeId = row.getString("OfficeId");} catch (JSONException e) {e.printStackTrace();OfficeId="";}
        try {this.OfficeName = row.getString("OfficeName");} catch (JSONException e) {e.printStackTrace();OfficeName="";}
    }

    public String getOfficeId() {
        return OfficeId;
    }

    public void setOfficeId(String officeId) {
        OfficeId = officeId;
    }

    public String getOfficeName() {
        return OfficeName;
    }

    public void setOfficeName(String officeName) {
        OfficeName = officeName;
    }
    @Override
    public String toString() {
        return OfficeName;
    }
}
