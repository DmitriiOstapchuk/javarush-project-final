package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.login.User;
import com.javarush.jira.login.UserTo;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.login.internal.web.UserTestData.*;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL_PROFILE = ProfileRestController.REST_URL;
    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getUserProfileTo() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_PROFILE))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(USER_PROFILE_TO));
    }

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void getGuestProfileTo() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_PROFILE))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(GUEST_PROFILE_EMPTY_TO));
    }

    @Test
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_PROFILE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        long user_id = 1;
        Profile updated = getUpdated(user_id);
        ProfileTo updatedTo = getUpdatedTo();
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Profile dbProfileAfter = profileRepository.getExisted(user_id);
        PROFILE_MATCHER.assertMatch(dbProfileAfter, updated);
    }

    @Test
    void updateUnauthorized() throws Exception {
        long user_id = 1;
        ProfileTo updatedUnauthTo = getUpdatedTo();
        updatedUnauthTo.setId(user_id);
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updatedUnauthTo)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void addNew() throws Exception {
        long user_id = 3;
        Profile newProfile = getNew(user_id);
        ProfileTo newProfileTo = getNewTo();
        newProfileTo.setId(user_id);
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newProfileTo)))
                .andDo(print())
                .andExpect(status().isNoContent());
        Profile dbProfileAfter = profileRepository.getExisted(user_id);
        PROFILE_MATCHER.assertMatch(dbProfileAfter, newProfile);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateInvalid() throws Exception {
        long user_id = 1;
        Profile existingProfile = profileRepository.getExisted(user_id);
        ProfileTo invalidProfileTo = getInvalidTo();
        invalidProfileTo.setId(user_id);
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(invalidProfileTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Profile dbProfileAfter = profileRepository.getExisted(user_id);
        PROFILE_MATCHER.assertMatch(dbProfileAfter, existingProfile);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateWithUnknownNotification() throws Exception {
        long user_id = 1;
        Profile existingProfile = profileRepository.getExisted(user_id);
        ProfileTo withUnknownNotificationProfileTo = getWithUnknownNotificationTo();
        withUnknownNotificationProfileTo.setId(user_id);
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(withUnknownNotificationProfileTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Profile dbProfileAfter = profileRepository.getExisted(user_id);
        PROFILE_MATCHER.assertMatch(dbProfileAfter, existingProfile);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateWithUnknownContact() throws Exception {
        long user_id = 1;
        Profile existingProfile = profileRepository.getExisted(user_id);
        ProfileTo withUnknownContactProfileTo = getWithUnknownContactTo();
        withUnknownContactProfileTo.setId(user_id);
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(withUnknownContactProfileTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Profile dbProfileAfter = profileRepository.getExisted(user_id);
        PROFILE_MATCHER.assertMatch(dbProfileAfter, existingProfile);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateWithContactHtmlUnsafe() throws Exception {
        long user_id = 1;
        Profile existingProfile = profileRepository.getExisted(user_id);
        ProfileTo withContactHtmlUnsafeProfileTo = getWithContactHtmlUnsafeTo();
        withContactHtmlUnsafeProfileTo.setId(user_id);
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(withContactHtmlUnsafeProfileTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        Profile dbProfileAfter = profileRepository.getExisted(user_id);
        PROFILE_MATCHER.assertMatch(dbProfileAfter, existingProfile);
    }
}