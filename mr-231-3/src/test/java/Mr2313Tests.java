import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.solution.searadar.mr231_3.convert.Mr231_3Converter;
import org.solution.searadar.mr231_3.station.Mr231_3StationType;
import ru.oogis.searadar.api.message.RadarSystemDataMessage;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import ru.oogis.searadar.api.message.TrackedTargetMessage;
import ru.oogis.searadar.api.types.IFF;
import ru.oogis.searadar.api.types.TargetStatus;
import ru.oogis.searadar.api.types.TargetType;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * <p>
 *     Класс с тестовыми методами для проверки конвертера МР-231-3
 * </p>
 *
 */
public class Mr2313Tests {

    /**
     *
     * <p>
     *     Переменная типа станции для МР-231-3
     * </p>
     *
     * @see Mr231_3StationType
     *
     */
    private final Mr231_3StationType st = new Mr231_3StationType();


    /**
     *
     * <p>
     *     Тест для проверки работы конвертера для сообщения {@link TrackedTargetMessage} с корректными данными
     * </p>
     *
     * @result Все поля сообщений совпадают, тест пройден
     * @see Mr231_3Converter
     * @see SearadarStationMessage
     * @see TrackedTargetMessage
     *
     */
    @Test
    void ConverterTTMTest_LegalData() {
        Mr231_3Converter converter = st.createConverter();
        TrackedTargetMessage expected = new TrackedTargetMessage();
        expected.setMsgTime(457362L);
        expected.setBearing(341.1);
        expected.setCourse(024.5);
        expected.setIff(IFF.FRIEND);
        expected.setDistance(28.71);
        expected.setSpeed(57.6);
        expected.setStatus(TargetStatus.LOST);
        expected.setTargetNumber(66);
        expected.setType(TargetType.UNKNOWN);
        expected.setMsgRecTime(new Timestamp(System.currentTimeMillis()));

        List<SearadarStationMessage> actual = converter.convert("$RATTM,66,28.71,341.1,T,57.6,024.5,T,0.4,4.1,N,b,L,,457362,А*42");

        TrackedTargetMessage act_ttm = (TrackedTargetMessage) actual.get(0);

        Assertions.assertEquals(expected.getMsgRecTime().getTime() / 1000, act_ttm.getMsgRecTime().getTime() / 1000);
        Assertions.assertEquals(expected.getBearing(), act_ttm.getBearing());
        Assertions.assertEquals(expected.getCourse(), act_ttm.getCourse());
        Assertions.assertEquals(expected.getIff(), act_ttm.getIff());
        Assertions.assertEquals(expected.getDistance(), act_ttm.getDistance());
        Assertions.assertEquals(expected.getSpeed(), act_ttm.getSpeed());
        Assertions.assertEquals(expected.getStatus(), act_ttm.getStatus());
        Assertions.assertEquals(expected.getMsgTime(), act_ttm.getMsgTime());
        Assertions.assertEquals(expected.getTargetNumber(), act_ttm.getTargetNumber());
        Assertions.assertEquals(expected.getType(), act_ttm.getType());
    }

    /**
     *
     * <p>
     *     Тест для проверки работы конвертера для сообщения {@link RadarSystemDataMessage} с корректными данными
     * </p>
     *
     * @result Все поля сообщений совпадают, тест пройден
     * @see Mr231_3Converter
     * @see RadarSystemDataMessage
     * @see SearadarStationMessage
     *
     */
    @Test
    void converterRSDTest_LegalData() {
        Mr231_3Converter converter = st.createConverter();
        RadarSystemDataMessage expected = new RadarSystemDataMessage();
        expected.setBearing(320.6);
        expected.setBearing2(185.3);
        expected.setDisplayOrientation("N");
        expected.setDistanceScale(96.0);
        expected.setDistanceFromShip(11.6);
        expected.setDistanceUnit("N");
        expected.setInitialBearing(331.4);
        expected.setInitialDistance(36.5);
        expected.setMovingCircleOfDistance(8.4);
        expected.setWorkingMode("S");
        expected.setMsgRecTime(new Timestamp(System.currentTimeMillis()));

        List<SearadarStationMessage> actual = converter.convert("$RARSD,36.5,331.4,8.4,320.6,,,,,11.6,185.3,96.0,N,N,S*20");

        Assertions.assertEquals(expected.toString(), actual.get(0).toString());
    }

    /**
     *
     * <p>
     *     Тест для проверки работы конвертера для сообщения {@link TrackedTargetMessage} с отсутствующими данными в строке
     * </p>
     *
     * @throws IndexOutOfBoundsException .
     * @result Выброс исключения {@link IndexOutOfBoundsException} при попытке конвертации
     * @see IndexOutOfBoundsException
     * @see Mr231_3Converter
     * @see RadarSystemDataMessage
     * @see SearadarStationMessage
     *
     */
    @Test
    void ConverterTTMTest_EmptyString() {
        Mr231_3Converter converter = st.createConverter();

        try {
            converter.convert("$RATTM,,,,,,,,,,,,,,,*");
        }
        catch (IndexOutOfBoundsException ex){
            Assertions.assertEquals("Index 12 out of bounds for length 1", ex.getMessage());
        }
    }

    /**
     *
     * <p>
     *     Тест для проверки работы конвертера для сообщения {@link RadarSystemDataMessage} с отсутствующими данными в строке
     * </p>
     *
     * @throws IndexOutOfBoundsException .
     * @result Выброс исключения {@link IndexOutOfBoundsException} при попытке конвертации
     * @see IndexOutOfBoundsException
     * @see Mr231_3Converter
     * @see RadarSystemDataMessage
     * @see SearadarStationMessage
     *
     */
    @Test
    void converterRSDTest_EmptyString() {
        Mr231_3Converter converter = st.createConverter();

        try{
            converter.convert("$RARSD,,,,,,,,,,,,,,*");
        }
        catch (IndexOutOfBoundsException ex){
            Assertions.assertEquals("Index 1 out of bounds for length 1", ex.getMessage());
        }
    }
}
