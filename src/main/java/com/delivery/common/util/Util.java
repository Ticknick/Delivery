package com.delivery.common.util;

import com.delivery.common.SedException;
import com.delivery.common.dao.OrderDao;
import com.delivery.common.dao.OrderLogDao;
import com.delivery.common.entity.OrderEntity;
import com.delivery.common.entity.OrderLogEntity;
import com.delivery.common.entity.UserEntity;
import com.delivery.common.ErrorCode;
import com.delivery.common.Response;
import com.delivery.event.EventContext;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.delivery.common.ErrorCode.*;
import static com.delivery.common.constant.Constant.*;

/**
 * @author finderlo
 * @date 21/04/2017
 */
public class Util {


    public static <T extends Enum<T>> T enumFromOrigin(int origin, Class<T> classT) {
        T[] values = classT.getEnumConstants();
        for (T t : values) {
            Enum ty = (Enum) t;
            if (ty.ordinal() == origin) {
                return t;
            }
        }
        return null;
    }

    public static void writeToResponse(HttpServletResponse response, Object o) throws IOException {
        response.getWriter().append(o.toString()).flush();
    }


    public static void saveOrderLog(String order_id, OrderEntity.OrderState orderState, OrderLogDao orderLogDao) {
        try {
            OrderLogEntity log = new OrderLogEntity();
            log.setOrderId(order_id);
            log.setChangeTime(new Timestamp(System.currentTimeMillis()));
            log.setOrderState(orderState);
            orderLogDao.save(log);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SedException(ORDER_OPERATION_LOG_PUBLISH_ERROR);
        }
    }



    public static Map<String, Object> entityToMap(Object object) {
        Map<String, Object> attr = new HashMap<>();
        if (object == null) {
            return attr;
        }
        Class clazz = object.getClass();
        try {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                attr.put(field.getName(), field.get(object));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return attr;
    }


    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static boolean isHaveEnoughInfoToUpgrade(UserEntity user) {
        try {
            Assert.isStringExist(user.getSchoolCard());
            Assert.isStringExist(user.getIdCard());
            Assert.isStringExist(user.getAliPay());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
