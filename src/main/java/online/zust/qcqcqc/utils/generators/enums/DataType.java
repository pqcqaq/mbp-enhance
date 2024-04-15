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
    Bigint(0, "bigint"),
    Binary(1, "binary"),
    Bit(2, "bit"),
    Blob(3, "blob"),
    Char(4, "char"),
    Date(5, "date"),
    Datetime(6, "datetime"),
    Decimal(7, "decimal"),
    Double(8, "double"),
    Enum(9, "enum"),
    Float(10, "float"),
    Geometry(11, "geometry"),
    GeometryCollection(12, "geometrycollection"),
    Int(13, "int"),
    Integer(14, "integer"),
    Json(15, "json"),
    LineString(16, "linestring"),
    LongBlob(17, "longblob"),
    LongText(18, "longtext"),
    MediumBlob(19, "mediumblob"),
    MediumInt(20, "mediumint"),
    MediumText(21, "mediumtext"),
    MultiLineString(22, "multilinestring"),
    MultiPoint(23, "multipoint"),
    MultiPolygon(24, "multipolygon"),
    Numeric(25, "numeric"),
    Point(26, "point"),
    Polygon(27, "polygon"),
    Real(28, "real"),
    Set(29, "set"),
    SmallInt(30, "smallint"),
    Text(31, "text"),
    Time(32, "time"),
    Timestamp(33, "timestamp"),
    TinyBlob(34, "tinyblob"),
    Tinyint(35, "tinyint"),
    TinyText(36, "tinytext"),
    VarBinary(37, "varbinary"),
    Varchar(38, "varchar"),
    Year(39, "year");
    final int type;
    final String name;

    DataType(int type, String name) {
        this.type = type;
        this.name = name;
    }

}
