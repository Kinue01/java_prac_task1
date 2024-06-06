package org.solution.searadar.mr231_3.convert;

import org.apache.camel.Exchange;
import ru.oogis.searadar.api.convert.SearadarExchangeConverter;
import ru.oogis.searadar.api.message.*;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * Класс конвертера для МР-231-3
 *
 */
public class Mr231_3Converter implements SearadarExchangeConverter {

    /**
     *
     * Массив размерностей для Шкалы дальности в сообщении {@link RadarSystemDataMessage}
     *
     */
    private static final Double[] DISTANCE_SCALE = {0.125, 0.25, 0.5, 1.5, 3.0, 6.0, 12.0, 24.0, 48.0, 96.0};

    /**
     *
     * Массив с данными из сообщения
     *
     */
    private String[] fields;

    /**
     *
     * Тип сообщения ({@link TrackedTargetMessage}, {@link RadarSystemDataMessage})
     *
     */
    private String msgType;

    /**
     *
     * <p>
     *     Метод-обёртка для метода, принимающий строку с сообщением
     * </p>
     * <p>
     *     Преобразует сообщение из класса Exchange в класс String
     * </p>
     *
     * @param exchange передаваемое сообщение типа {@link Exchange}
     * @return список сообщений {@link SearadarStationMessage}
     * @see SearadarStationMessage
     *
     */
    @Override
    public List<SearadarStationMessage> convert(Exchange exchange) {
        return convert(exchange.getIn().getBody(String.class));
    }

    /**
     *
     * <p>
     *     Метод конвертации строки в список сообщений {@link SearadarStationMessage}
     * </p>
     *
     * @param message передаваемое сообщение
     * @return список сообщений {@link SearadarStationMessage}
     * @see SearadarStationMessage
     *
     */
    public List<SearadarStationMessage> convert(String message) {

        List<SearadarStationMessage> msgList = new ArrayList<>();

        readFields(message);

        switch (msgType) {

            case "TTM" : msgList.add(getTTM());
                break;

            case "RSD" : {

                RadarSystemDataMessage rsd = getRSD();
                InvalidMessage invalidMessage = checkRSD(rsd);

                if (invalidMessage != null)  msgList.add(invalidMessage);
                else msgList.add(rsd);
                break;
            }

        }

        return msgList;
    }

    /**
     *
     * <p>
     *     Метод, который формирует массив данных из строки
     * </p>
     * <p>
     *     Дополнительно вычленяется тип сообщения
     * </p>
     *
     * @param msg передаваемое сообщение
     *
     */
    private void readFields(String msg) {

        String temp = msg.substring( 3, msg.indexOf("*") ).trim();

        fields = temp.split(Pattern.quote(","));
        msgType = fields[0];

    }

    /**
     *
     * <p>
     *     Метод, который, обращаясь к массиву данных, генерирует сообщение {@link TrackedTargetMessage}
     * </p>
     * <p>
     *     При этом тип цели остаётся неизвестен
     * </p>
     *
     * @throws IndexOutOfBoundsException если сообщение пустое
     * @return сообщение {@link TrackedTargetMessage}
     * @see TrackedTargetMessage
     *
     */
    private TrackedTargetMessage getTTM() {
        TrackedTargetMessage ttm = new TrackedTargetMessage();

        ttm.setMsgRecTime(new Timestamp(System.currentTimeMillis()));

        TargetStatus status = TargetStatus.UNRELIABLE_DATA;
        IFF iff = IFF.UNKNOWN;
        TargetType type = TargetType.UNKNOWN;

        switch (fields[12]) {
            case "L" : status = TargetStatus.LOST;
                break;

            case "Q" : status = TargetStatus.UNRELIABLE_DATA;
                break;

            case "T" : status = TargetStatus.TRACKED;
                break;
        }

        switch (fields[11]) {
            case "b" : iff = IFF.FRIEND;
                break;

            case "p" : iff = IFF.FOE;
                break;

            case "d" : iff = IFF.UNKNOWN;
                break;
        }

        ttm.setTargetNumber(Integer.parseInt(fields[1]));
        ttm.setDistance(Double.parseDouble(fields[2]));
        ttm.setBearing(Double.parseDouble(fields[3]));
        ttm.setCourse(Double.parseDouble(fields[6]));
        ttm.setSpeed(Double.parseDouble(fields[5]));
        ttm.setStatus(status);
        ttm.setIff(iff);
        ttm.setMsgTime(Long.parseLong(fields[14]));
        ttm.setType(type);

        return ttm;
    }

    /**
     *
     * <p>
     *     Метод формирования сообщения {@link RadarSystemDataMessage} по данным из массива @code fields[]
     * </p>
     *
     * @throws IndexOutOfBoundsException если сообщение пустое
     * @return сообщение {@link RadarSystemDataMessage}
     * @see RadarSystemDataMessage
     *
     */
    private RadarSystemDataMessage getRSD() {

        RadarSystemDataMessage rsd = new RadarSystemDataMessage();

        rsd.setMsgRecTime(new Timestamp(System.currentTimeMillis()));
        rsd.setInitialDistance(Double.parseDouble(fields[1]));
        rsd.setInitialBearing(Double.parseDouble(fields[2]));
        rsd.setMovingCircleOfDistance(Double.parseDouble(fields[3]));
        rsd.setBearing(Double.parseDouble(fields[4]));
        rsd.setDistanceFromShip(Double.parseDouble(fields[9]));
        rsd.setBearing2(Double.parseDouble(fields[10]));
        rsd.setDistanceScale(Double.parseDouble(fields[11]));
        rsd.setDistanceUnit(fields[12]);
        rsd.setDisplayOrientation(fields[13]);
        rsd.setWorkingMode(fields[14]);

        return rsd;
    }

    /**
     *
     * <p>
     *     Метод, который проверяет корректность сообщения {@link RadarSystemDataMessage} по полю Шкала дальности
     * </p>
     * <p>
     *     Размерность шкалы должна совпадать с одним из значений в массиве доступных размерностей
     * </p>
     *
     * @param rsd сообщение {@link RadarSystemDataMessage}
     * @return сообщение {@link InvalidMessage}, либо @code null
     * @see InvalidMessage
     *
     */
    private InvalidMessage checkRSD(RadarSystemDataMessage rsd) {

        InvalidMessage invalidMessage = new InvalidMessage();
        String infoMsg = "";

        if (!Arrays.asList(DISTANCE_SCALE).contains(rsd.getDistanceScale())) {

            infoMsg = "RSD message. Wrong distance scale value: " + rsd.getDistanceScale();
            invalidMessage.setInfoMsg(infoMsg);
            return invalidMessage;
        }

        return null;
    }

}
