package com.naeggeodo.dto;

import com.naeggeodo.entity.chat.RemittanceState;
import com.naeggeodo.interfaces.JSONConverterAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserNameIdDTO extends JSONConverterAdapter {
    private String user_id;
    private String nickname;

    private RemittanceState state;

    @Override
    public JSONObject toJSON() throws Exception {
        JSONObject json =  new JSONObject();
        json.put("user_id",user_id);
        json.put("nickname",nickname);
        json.put("remittanceState",state);
        return json;
    }
}
