package online.zust.qcqcqc.utils.generators.enums;

/**
 * @author qcqcqc
 * @date 2024/04
 * @time 17-19-49
 */
public enum DataType {
    /**
     * bigint
     */
    Bigint(0, "bigint", false),
    Binary(1, "binary", true),
    Bit(2, "bit", true),
    Blob(3, "blob", true),
    Char(4, "char", true),
    Date(5, "date", false),
    Datetime(6, "datetime", false),
    Decimal(7, "decimal", true),
    Double(8, "double", true),
    Enum(9, "enum", true),
    Float(10, "float", true),
    Geometry(11, "geometry", false),
    GeometryCollection(12, "geometrycollection", false),
    Int(13, "int", true),
    Integer(14, "integer", true),
    Json(15, "json", false),
    LineString(16, "linestring", false),
    LongBlob(17, "longblob", false),
    LongText(18, "longtext", false),
    MediumBlob(19, "mediumblob", false),
    MediumInt(20, "mediumint", true),
    MediumText(21, "mediumtext", false),
    MultiLineString(22, "multilinestring", false),
    MultiPoint(23, "multipoint", false),
    MultiPolygon(24, "multipolygon", false),
    Numeric(25, "numeric", true),
    Point(26, "point", false),
    Polygon(27, "polygon", false),
    Real(28, "real", true),
    Set(29, "set", true),
    SmallInt(30, "smallint", true),
    Text(31, "text", false),
    Time(32, "time", false),
    Timestamp(33, "timestamp", false),
    TinyBlob(34, "tinyblob", false),
    Tinyint(35, "tinyint", true),
    TinyText(36, "tinytext", false),
    VarBinary(37, "varbinary", true),
    Varchar(38, "varchar", true),
    Year(39, "year", false);
    final int type;
    final String name;
    final boolean hasLength;

    DataType(int type, String name, boolean hasLength) {
        this.type = type;
        this.name = name;
        this.hasLength = hasLength;
    }

    public String getType() {
        return name;
    }

    public boolean hasLength() {
        return hasLength;
    }

    public int getDefaultLength() {
        return 255;
    }
}
