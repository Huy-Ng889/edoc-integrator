package com.bkav.edoc.util;

import com.bkav.edoc.service.commonutil.Checker;
import com.bkav.edoc.service.kernel.util.Validator;
import com.bkav.edoc.service.redis.RedisKey;
import com.bkav.edoc.service.redis.RedisUtil;
import com.bkav.edoc.service.xml.base.header.CheckPermission;
import com.bkav.edoc.service.xml.base.header.Error;
import com.bkav.edoc.service.xml.base.header.Report;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class EdocUtil {
    private final static Checker CHECKER = new Checker();

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        Map<String, String> map = new HashMap<>();
        while (headers.hasMoreElements()) {
            String key = headers.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static void saveEdxmlFilePathToCache(long documentId, String filePath) {
        try {
            RedisUtil.getInstance().set(RedisKey.getKey(String.valueOf(documentId), RedisKey.GET_DOCUMENT_EDXML_KEY), filePath);
            LOGGER.info("Save success file path " + filePath + " to cache for documentId " + documentId);
        } catch (Exception e) {
            LOGGER.error("Error save EdXML file path to cache with document id " + documentId + " cause " + e.getMessage());
        }
    }

    public static List<Error> validateHeader(Map<String, String> header) {
        List<Error> errors = new ArrayList<>();
        if (header.size() <= 0) {
            LOGGER.error("------------ Error cause header is null !!!!");
            errors.add(new Error("Invalid Credentials", "Invalid Credentials. Make sure you have provided the correct security credentials"));
            errors.add(new Error("Invalid Credentials", "Invalid Credentials. Make sure you have provided the correct security credentials"));
        } else {
            String organId = header.get(EdocServiceConstant.ORGAN_ID);
            String token = header.get(EdocServiceConstant.TOKEN);
            if (Validator.isNullOrEmpty(organId)) {
                LOGGER.error("----------- Error header: organ id is null -------------");
                errors.add(new Error("Invalid Credentials", "Invalid Credentials. Make sure you have provided the correct security credentials"));
            }
            if (Validator.isNullOrEmpty(token)) {
                LOGGER.error("----------- Error header: token id is null -------------");
                errors.add(new Error("Invalid Credentials", "Invalid Credentials. Make sure you have provided the correct security credentials"));
            }
        }
        if (errors.size() == 0) {
            String organId = header.get(EdocServiceConstant.ORGAN_ID);
            String token = header.get(EdocServiceConstant.TOKEN);
            CheckPermission checkPermission = new CheckPermission();
            checkPermission.setOrganId(organId);
            checkPermission.setToken(token);
            Report permission = CHECKER.checkPermission(checkPermission);
            if (!permission.isIsSuccess()) {
                errors.add(new Error("Unauthorized", "Unauthorized !"));
            }
        }
        return errors;
    }

    private final static Logger LOGGER = Logger.getLogger(EdocUtil.class);
}
