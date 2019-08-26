package ng.mobilea.airvoucher.utilities;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ng.mobilea.airvoucher.R;

/**
 * Created by Hp on 6/8/2017.
 */

public class DataFactory {
    private static final String PATTERN = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}";
    public static final String objectToString(Object object) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        try {
            String jsonData = mapper.writeValueAsString(object);
            return jsonData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDouble(double value) {
        NumberFormat format = DecimalFormat.getInstance();
        format.setRoundingMode(RoundingMode.FLOOR);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(3);
        return format.format(value).replace(",", "");
    }

    public static final String[] splitString(String input, String criteria) {
        String[] outPut = input.split("\\" + criteria);
        return outPut;
    }

    public static final String formatDate(Date date) throws Exception {
        DateFormat dFormat = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
        return dFormat.format(date);
    }

    public static final String formatDateTime(Date date) throws Exception {
        DateFormat dFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dFormat.format(date);
    }

    public static final Date parseDateTime(String date) throws Exception {
        DateFormat dFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dFormat.parse(date);
    }

    public static final Date parseStringDateTime(String date) throws Exception {
        DateFormat dFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dFormat.parse(date);
    }

    public static final List<Object> stringToObjectList(Class className, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        try {
            return mapper.readValue(jsonString, mapper.getTypeFactory().constructCollectionType(List.class, className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Object stringToObject(Class className, String jsonString) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        try {
            return mapper.readValue(jsonString, className);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isValidEmail(String target) {
        return target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(String target) {
        return target != null && Patterns.PHONE.matcher(target).matches();
    }

    public static String formatPhone(String phone, Context context) throws Exception {
        String countryCode = getCountryZipCode(context);
        if (countryCode.equals("250")) {
            String firstDigit = phone.substring(0, 3);
            if (phone.length() >= 12 && firstDigit.equals("250"))
                return phone;
            else if (phone.length() <= 10 && !firstDigit.equals("250")) {
                if (phone.substring(0, 1).contains("0"))
                    return "25" + phone;
                else
                    return "250" + phone;
            } else
                throw new Exception("Invalid phone");
        } else {
            String inPutCode = phone.substring(0, countryCode.length() - 1);
            if (!countryCode.equals(inPutCode)) {
                throw new Exception("prefix " + countryCode);
            }
            if (isValidPhone(phone))
                return phone;
            else
                throw new Exception("Invalid phone");
        }

    }

    public static String getCountryZipCode(Context context) {
        String countryID = "";
        String countryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        countryID = manager.getNetworkCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(countryID.trim())) {
                countryZipCode = g[0];
                break;
            }
        }
        return countryZipCode;
    }

    public static boolean isStrongPassword(String password)
    {
        return password.matches(PATTERN);
    }
}
