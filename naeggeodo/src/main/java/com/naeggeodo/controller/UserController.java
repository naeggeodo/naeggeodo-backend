package com.naeggeodo.controller;

import com.naeggeodo.dto.AddressDTO;
import com.naeggeodo.dto.MypageDTO;
import com.naeggeodo.dto.NicknameDTO;
import com.naeggeodo.dto.ResponseDTO;
import com.naeggeodo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.naeggeodo.util.ResponseUtils.success;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping(value = "/user/{user_id}/address", produces = "application/json")
    public ResponseDTO<AddressDTO> updateAddress(@PathVariable("user_id") String user_id
            , @RequestBody @Valid AddressDTO dto) {
        AddressDTO addressDTO = userService.updateUserAddress(user_id, dto);
        return success(addressDTO);
    }

    @GetMapping(value = "/user/{user_id}/address", produces = "application/json")
    public ResponseDTO<AddressDTO> getAddress(@PathVariable("user_id") String user_id) {
        AddressDTO addressDTO = userService.findAddressById(user_id);
        return success(addressDTO);
    }

    @GetMapping(value = "/user/{user_id}/mypage", produces = "application/json")
    public ResponseDTO<MypageDTO> getMyPageData(@PathVariable("user_id") String user_id) {
        MypageDTO mypageDTO = userService.getMyPageData(user_id);
        return success(mypageDTO);
    }

    //해당 유저 퀵채팅
    @GetMapping(value = "/user/{user_id}/quick-chatting", produces = "application/json")
    public ResponseDTO<List<String>> getQuickChat(@PathVariable(name = "user_id") String user_id) {
        List<String> quickChatList = userService.getQuickChat(user_id);
        return success(quickChatList);
    }


    @PatchMapping(value = "/user/{user_id}/quick-chatting", produces = "application/json")
    public ResponseDTO<List<String>> updateQuickChat(@PathVariable("user_id") String user_id,
                                                     @RequestBody List<String> messages) {
        List<String> list = userService.updateQuickChat(user_id, messages);
        return success(list);
    }

    @GetMapping(value = "/user/{user_id}/nickname", produces = "application/json")
    public ResponseDTO<NicknameDTO> getNickname(@PathVariable(name = "user_id") String user_id) {
        NicknameDTO nicknameDTO = userService.getNickName(user_id);
        return success(nicknameDTO);
    }

    @PatchMapping(value = "/user/{user_id}/nickname", produces = "application/json")
    public ResponseDTO<NicknameDTO> updateNickname(@PathVariable(name = "user_id") String user_id,
                                                   @RequestParam(name = "value") String value) {
        NicknameDTO nicknameDTO = userService.updateNickName(user_id, value);
        return success(nicknameDTO);
    }
}
