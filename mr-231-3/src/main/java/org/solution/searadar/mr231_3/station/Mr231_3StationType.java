package org.solution.searadar.mr231_3.station;

import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.solution.searadar.mr231_3.convert.Mr231_3Converter;
import java.nio.charset.Charset;

/**
 *
 * <p>
 *     Класс типа станции МР-231-3
 * </p>
 *
 */
public class Mr231_3StationType {

    /**
     *
     * <p>
     *     Строковая константа с типом станции
     * </p>
     *
     */
    private static final String STATION_TYPE = "МР-231-3";

    /**
     *
     * <p>
     *     Строковая константа с названием кодека
     * </p>
     *
     */
    private static final String CODEC_NAME = "mr231-3";

    /**
     *
     * <p>
     *     Метод инициализации, который не используется
     * </p>
     *
     */
    protected void doInitialize() {
        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(
                Charset.defaultCharset(),
                LineDelimiter.UNIX,
                LineDelimiter.CRLF
        );
    }

    /**
     *
     * <p>
     *     Метод, который создаёт экземпляр конвертера {@link Mr231_3Converter} для МР-231-3
     * </p>
     *
     * @return экземпляр конвертера
     * @see Mr231_3Converter
     */
    public Mr231_3Converter createConverter() {
        return new Mr231_3Converter();
    }
}