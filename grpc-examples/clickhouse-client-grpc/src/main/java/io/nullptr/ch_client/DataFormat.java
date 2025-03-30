package io.nullptr.ch_client;

import lombok.Getter;

@Getter
public enum DataFormat {

    TAB_SEPARATED("TabSeparated"),
    TAB_SEPARATED_WITH_NAMES_AND_TYPES("TabSeparatedWithNamesAndTypes"),
    PROTOBUF("Protobuf"),
    ROW_BINARY("RowBinary"),
    ROW_BINARY_WITH_NAMES_AND_TYPES("RowBinaryWithNamesAndTypes"),
    JSON("JSON"),
    NATIVE("Native"),
    LINE_AS_STRING("LineAsString");

    private final String format;

    DataFormat(String format) {
        this.format = format;
    }

}
